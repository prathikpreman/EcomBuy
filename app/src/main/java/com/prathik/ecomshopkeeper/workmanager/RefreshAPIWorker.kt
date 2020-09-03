package com.prathik.ecomshopkeeper.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.prathik.ecomshopkeeper.network.ApiClient
import com.prathik.ecomshopkeeper.network.models.products.ProductModels
import com.prathik.ecomshopkeeper.realm.ProductRealmInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class RefreshAPIWorker(context: Context,params: WorkerParameters) : Worker(context,params) {

    companion object{
        const val KEY_COUNT_VALUE = "key_count"
        const val KEY_WORKER = "key_worker"
        const val TAG = "worker_tag"
    }

    override fun doWork(): Result {

        try {
            val count = inputData.getInt(KEY_COUNT_VALUE,0)
//            for (i in 0 until count) {
//                Log.i("MYTAG", "Uploading $i")
//            }
//
//            val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//            val currentDate = time.format(Date())


            val call: Call<ProductModels> = ApiClient.getClient.getAllProducts()
            call.enqueue(object : Callback<ProductModels> {

                override fun onFailure(call: Call<ProductModels>, t: Throwable) {
                    Log.d(TAG,"API Error: ${t.message}")
                }

                override fun onResponse(call: Call<ProductModels>, response: Response<ProductModels>) {

                    Log.d(TAG,"URL :${response.raw().request().url()}")
                    val successResponse: String =  Gson().toJson(response.body())
                    Log.d(TAG, "successResponse: $successResponse")

                    var products: ProductModels? = response.body()
                    ProductRealmInstance.deleteAllProducts()
                    ProductRealmInstance.addToProduct(products)
                }
            })

//            val outPutData = Data.Builder()
//                .putString(KEY_WORKER,currentDate)
//                .build()

            return Result.success()

        }catch (e:Exception){

            return Result.failure()
        }
    }

}
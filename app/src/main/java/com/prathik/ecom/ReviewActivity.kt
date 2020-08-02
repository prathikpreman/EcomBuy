package com.prathik.ecom

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.prathik.ecom.adapter.CartAdapter
import com.prathik.ecom.adapter.ReviewAdapter
import com.prathik.ecom.network.ApiClient
import com.prathik.ecom.network.models.appsettings.GetAppSettings
import com.prathik.ecom.network.models.category.CategoriesDetailsItem
import com.prathik.ecom.network.models.category.GetAllCategory
import com.prathik.ecom.realm.CartRealmObject
import com.prathik.ecom.realm.CategoryRealmInstance
import com.prathik.ecom.realm.ProductRealmInstance
import com.prathik.ecom.realm.products.ProductsModelR
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kotlinx.android.synthetic.main.white_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewActivity : AppCompatActivity() {

    private lateinit var cartAdapter: ReviewAdapter
    private  var modelArrayList: RealmResults<ProductsModelR>?= null

    val TAG="ReviewActivity"

    private fun init() {
        supportActionBar?.hide()
        whiteToolbarBack.visibility= View.VISIBLE

        modelArrayList = ProductRealmInstance.getAllProducts()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        cartAdapter = ReviewAdapter(this, modelArrayList)
        reviewRv?.adapter = cartAdapter

        reviewRv.layoutManager = layoutManager
        reviewRv.itemAnimator = DefaultItemAnimator()

      //  cart_total_tv.text="Cart Total: ${getString(R.string.rupee)} ${CartRealmObject.getCartTotal()}"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        init()

        if(ProductRealmInstance.isCartItemAvaliable()){
            modelArrayList=ProductRealmInstance.getAllProducts()

            var grandTotal=CartRealmObject.getCartTotal()
            subTotal.text="${getString(R.string.rupee)} $grandTotal"
        }

        getAppSettings()
    }

    fun getAppSettings(){

        grandTotalLoadingLayout.visibility=View.VISIBLE
        grandTotalLayout.visibility=View.GONE
        setPlaceOrderButton(false)
        var grandTotal=0.0F

        val call: Call<GetAppSettings> = ApiClient.getClient.getAppSettings()
        call.enqueue(object : Callback<GetAppSettings> {
            override fun onFailure(call: Call<GetAppSettings>, t: Throwable) {
                Log.d(TAG,"API Error Category: ${t.message}")
                Toast.makeText(this@ReviewActivity,"Error: ${t.message}",Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onResponse(call: Call<GetAppSettings>, response: Response<GetAppSettings>) {

                grandTotalLoadingLayout.visibility=View.GONE
                grandTotalLayout.visibility=View.VISIBLE
                setPlaceOrderButton(true)

                 Log.d(TAG,"URL :${response.raw().request().url()}")
                val successResponse: String =  Gson().toJson(response.body())
                Log.d(TAG, "successResponse: $successResponse")

                val appSettings: GetAppSettings? = response.body()

                if(appSettings!=null){

                    Log.d(TAG, "inside APp Seetings:")
                    grandTotal=CartRealmObject.getCartTotal()

                    var taxPercentage=appSettings.appSettings.tax

                    tax.text="${getString(R.string.rupee)} ${grandTotal*(taxPercentage/100)} (${taxPercentage}%)"

                    grandTotal=getPercentage(grandTotal,taxPercentage)
                    grandTotal+=appSettings.appSettings.deliveryCharge


                    reviewGrandTotal.text="${getString(R.string.rupee)} $grandTotal"
                    deliveryCharge.text="${getString(R.string.rupee)} ${(appSettings.appSettings.deliveryCharge).toString()} "
                }else{
                    Log.d(TAG, "inside APp Not Seetings:")
                }


            }

        })

    }

    fun setPlaceOrderButton(isEnable:Boolean){

        placeOrder.isEnabled=isEnable
        placeOrder.isClickable=isEnable

        if(isEnable){
            placeOrder.alpha=1.0F
        }else{
            placeOrder.alpha=0.4F
        }

    }


    fun getPercentage(total:Float,percentage:Float):Float{
        return total+((percentage/100)*total)
    }


}
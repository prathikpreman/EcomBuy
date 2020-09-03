package com.prathik.ecomshopkeeper.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.google.gson.Gson
import com.prathik.ecomshopkeeper.MainActivity
import com.prathik.ecomshopkeeper.R
import com.prathik.ecomshopkeeper.SearchActivity
import com.prathik.ecomshopkeeper.SetLocation
import com.prathik.ecomshopkeeper.adapter.CategoryAdapter
import com.prathik.ecomshopkeeper.adapter.FoodItemAdapter
import com.prathik.ecomshopkeeper.adapter.HomeSliderAdapter
import com.prathik.ecomshopkeeper.adapter.model.ScrollDatas
import com.prathik.ecomshopkeeper.adapter.model.SliderItem
import com.prathik.ecomshopkeeper.network.ApiClient
import com.prathik.ecomshopkeeper.network.models.cart.CartItem
import com.prathik.ecomshopkeeper.network.models.cart.GetCartModel
import com.prathik.ecomshopkeeper.network.models.category.CategoriesDetailsItem
import com.prathik.ecomshopkeeper.network.models.category.GetAllCategory
import com.prathik.ecomshopkeeper.network.models.products.ProductModels
import com.prathik.ecomshopkeeper.realm.CartRealmObject
import com.prathik.ecomshopkeeper.realm.CategoryRealmInstance
import com.prathik.ecomshopkeeper.realm.products.ProductsModelR
import com.prathik.ecomshopkeeper.realm.ProductRealmInstance
import com.prathik.ecomshopkeeper.realm.products.CategoryModelR
import com.prathik.ecomshopkeeper.utils.AppConstants
import com.prathik.ecomshopkeeper.utils.PreferenceManager
import com.prathik.ecomshopkeeper.workmanager.RefreshAPIWorker
import com.prathik.ecomshopkeeper.workmanager.RefreshAPIWorker.Companion.KEY_COUNT_VALUE
import com.prathik.ecomshopkeeper.workmanager.RefreshAPIWorker.Companion.KEY_WORKER
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController.ClickListener
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.white_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(),FoodItemAdapter.OnProuctAddedListener {


    private var viewScrolled = 0f
    private val TAG="HomeFragment12"

    private lateinit var foodAdapter: FoodItemAdapter
    private  var modelArrayList: RealmResults<ProductsModelR> ?= null

    private lateinit var categoryAdapter: CategoryAdapter
    private  var categoryList: RealmResults<CategoryModelR> ?= null
    lateinit var cartRealmObject:CartRealmObject

    private fun init() {
        cartRealmObject=CartRealmObject
         modelArrayList  = ProductRealmInstance.getAllProducts()
         categoryList= CategoryRealmInstance.getAllCategory()

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        foodAdapter = FoodItemAdapter(activity as Context, modelArrayList,this)
        homeRecyclerView?.adapter = foodAdapter

        val gridLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity,3)
        categoryAdapter = CategoryAdapter(activity as Context, categoryList)
        categoryRecyclerView?.adapter = categoryAdapter


        homeRecyclerView.layoutManager = layoutManager
        homeRecyclerView.itemAnimator = DefaultItemAnimator()

        categoryRecyclerView.layoutManager = gridLayoutManager
        categoryRecyclerView.itemAnimator = DefaultItemAnimator()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       return inflater.inflate(R.layout.fragment_home, container, false)

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setToolbar()
        setAddressClick()
        setSlider()
        setNestedScrollViewListener()
        setDeliveryLocation()

        if (ProductRealmInstance.isProductAvaiableinRealm()){
            homeShimmer.stopShimmer()
            loadProductFromRealm()
            Log.d(TAG,"Started with Realm")

            Timer("SettingUp", false).schedule(5000) {
                Log.d(TAG,"TIMER STARTED")
                getProductsfromAPI()
            }
        }
        else{
            homeShimmer.startShimmer()
            getProductsfromAPI()
        }







     //   periodicRequest()


    }


    private fun setDeliveryLocation(){
        var locationAddress=PreferenceManager.getString(PreferenceManager.ADDRESS_LOCALITY,"")
        if(locationAddress=="" || locationAddress==null){
            locationTv.text="Set Delivery Location"
        }else{
            locationTv.text=locationAddress
        }

    }



    private fun setCartChangedListener(){
        cartRealmObject.getCartSizeListener(object :CartRealmObject.OnCartSizeChangedListener{
            override fun onCartSizeChanged(size: Int) {
                Log.d(TAG,"Cart Changed !!!")
                if (size > 0) {
                    (activity as MainActivity).cartTab.setBadgeCount(size)
                } else {
                    (activity as MainActivity).cartTab.removeBadge()
                }
            }
        })
    }




    private fun setToolbar(){
        if(PreferenceManager.getString(PreferenceManager.USER_NAME,"")==""){
            whiteToolbarText.text="Hi Guest"
        }else{
            whiteToolbarText.text="Hi ${PreferenceManager.getString(PreferenceManager.USER_NAME,"")}"
        }

        whiteToolbarBack.visibility=View.GONE

        whiteToolbarSearch.setOnClickListener {
            startActivity(Intent(activity,SearchActivity::class.java))
        }

    }

    override fun onPause() {
        super.onPause()
        homseSlider.stopAutoCycle()
        cartRealmObject.removeAllProductListeners()
    }

    override fun onResume() {
        super.onResume()
        homseSlider.startAutoCycle()

    }

    override fun onStart() {
        super.onStart()
        setCartChangedListener()
    }

    private fun setNestedScrollViewListener(){

           homeNestedScrollView.viewTreeObserver.addOnScrollChangedListener {
               try {
                   if (viewScrolled < homeNestedScrollView.scrollY) {
                       viewScrolled = homeNestedScrollView.scrollY.toFloat()
                       Log.d(TAG, "scrolling up ");
                    //   EventBus.getDefault().post(ScrollDatas(AppConstants.DIRECTION_UP,isViewVisible(offer_text)))
                        EventBus.getDefault().post(ScrollDatas(AppConstants.DIRECTION_UP,true))

                   } else if (viewScrolled > homeNestedScrollView.scrollY) {
                       viewScrolled = homeNestedScrollView.scrollY.toFloat()
                       Log.d(TAG, "scrolling down")
                     //  EventBus.getDefault().post(ScrollDatas(AppConstants.DIRECTION_DOWN,isViewVisible(offer_text)))
                      EventBus.getDefault().post(ScrollDatas(AppConstants.DIRECTION_DOWN,true))
                   }

                   Log.d(TAG," VISIBILITY: ${isViewVisible(offer_text)}")

                  }
                   catch (e:Exception){
                       Log.d(TAG,"Exception on scroll: ${e.message}")
                   }
           }
       }

    private fun setAddressClick(){
        locationSelector.setOnClickListener {
            startActivity(Intent(activity,SetLocation::class.java))
        }
    }


    private fun setSlider(){


        val adapter = HomeSliderAdapter(activity)
        homseSlider.setSliderAdapter(adapter);
        homseSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        homseSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        homseSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH;
        homseSlider.indicatorSelectedColor = Color.WHITE
        homseSlider.indicatorUnselectedColor = Color.GRAY
        homseSlider.scrollTimeInSec = 3
        homseSlider.isAutoCycle = true
        homseSlider.startAutoCycle()

        var sliderItemList: ArrayList<SliderItem> = ArrayList()
        var sliderItem = SliderItem("","https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500")
        sliderItemList.add(sliderItem)
        sliderItem = SliderItem("","https://images.pexels.com/photos/239581/pexels-photo-239581.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500")
        sliderItemList.add(sliderItem)
        sliderItem = SliderItem("","https://images.pexels.com/photos/1414651/pexels-photo-1414651.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500")
        sliderItemList.add(sliderItem)
        adapter.renewItems(sliderItemList)

        homseSlider.setOnIndicatorClickListener(ClickListener {
            Log.i(TAG, "onIndicatorClicked: " + homseSlider.currentPagePosition)
        })



    }


    private fun addToCartAPI(productId:String,count:Int){
        var userId=PreferenceManager.getString(PreferenceManager.USER_ID,"")
        val call: Call<CartItem> = ApiClient.getClient.addToCart(userId.toString(),productId,count)
        call.enqueue(object :Callback<CartItem>{
            override fun onFailure(call: Call<CartItem>, t: Throwable) {
                Log.d(TAG,"API Error: ${t.message}")
            }

            override fun onResponse(call: Call<CartItem>, response: Response<CartItem>) {
                Log.d(TAG,"URL :${response.raw().request().url()}")
                val successResponse: String =  Gson().toJson(response.body())
                Log.d(TAG, "successResponse: $successResponse")

            }

        })
    }

    private fun removeFromcartAPI(productId:String){
        var userId=PreferenceManager.getString(PreferenceManager.USER_ID,"")
        val call: Call<GetCartModel> = ApiClient.getClient.removeFromCart(userId.toString(),productId)
        call.enqueue(object :Callback<GetCartModel>{
            override fun onFailure(call: Call<GetCartModel>, t: Throwable) {
                Log.d(TAG,"API Error: ${t.message}")
            }

            override fun onResponse(call: Call<GetCartModel>, response: Response<GetCartModel>) {
                Log.d(TAG,"URL :${response.raw().request().url()}")
                val successResponse: String =  Gson().toJson(response.body())
                Log.d(TAG, "successResponse: $successResponse")
            }

        })




    }


    private fun getProductsfromAPI()  {

            val parentJob = Job()
            val coroutineScope = CoroutineScope(Dispatchers.Default + parentJob)

        coroutineScope.async {

            val call: Call<ProductModels> = ApiClient.getClient.getAllProducts()
            call.enqueue(object : Callback<ProductModels> {

                override fun onFailure(call: Call<ProductModels>, t: Throwable) {
                    Log.d(TAG,"API Error Products: ${t.message}")
                    homeShimmer?.stopShimmer()
                }

                override fun onResponse(call: Call<ProductModels>, response: Response<ProductModels>) {
                    homeShimmer?.stopShimmer()

                    Log.d(TAG,"URL :${response.raw().request().url()}")
                    val successResponse: String =  Gson().toJson(response.body())
                    Log.d(TAG, "successResponse: $successResponse")

                    var products: ProductModels? = response.body()

                    //GetRealmInstance.deleteAllProducts()
                    ProductRealmInstance.addToProduct(products)
                    modelArrayList = ProductRealmInstance.getAllProducts()
                }
            })
        }

        coroutineScope.async {

            val call: Call<GetAllCategory> = ApiClient.getClient.getAllCategory()
            call.enqueue(object : Callback<GetAllCategory>{
                override fun onFailure(call: Call<GetAllCategory>, t: Throwable) {
                    Log.d(TAG,"API Error Category: ${t.message}")
                }
                override fun onResponse(call: Call<GetAllCategory>, response: Response<GetAllCategory>) {

                    Log.d(TAG,"URL :${response.raw().request().url()}")
                    val successResponse: String =  Gson().toJson(response.body())
                    Log.d(TAG, "successResponse: $successResponse")

                    var categories: GetAllCategory? = response.body()

                    CategoryRealmInstance.addCategory(categories?.categoriesDetails as List<CategoriesDetailsItem>)
                }

            })
        }


    }



    fun loadProductFromRealm(){
        modelArrayList = ProductRealmInstance.getAllProducts()
  //      foodAdapter = FoodItemAdapter(activity as Context, modelArrayList,this)
    //    homeRecyclerView?.adapter = foodAdapter
    }

    override fun onProductAdded(count: Int, productId: String) {
            ProductRealmInstance.updateCartByProductId(productId,count)
    }


    private fun isViewVisible(view:View):Boolean {
        val scrollBounds = Rect()
        homeNestedScrollView.getDrawingRect(scrollBounds)
        val top = view.getY()
        val bottom = top + view.getHeight()
        return scrollBounds.top < top && scrollBounds.bottom > bottom
    }

    fun periodicRequest(){

        val workManager = WorkManager.getInstance(context as Activity)

        workManager.cancelAllWorkByTag(KEY_COUNT_VALUE)

//        val data: Data = Data.Builder()
//            .putInt(KEY_COUNT_VALUE,20000)
//            .build()

        val constraints = Constraints.Builder()
         //   .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val periodicWorkRequest = PeriodicWorkRequest
            .Builder(RefreshAPIWorker::class.java,16, TimeUnit.MINUTES)
            .addTag(KEY_COUNT_VALUE)
            .setConstraints(constraints)
         //   .setInputData(data)
            .build()

         workManager.enqueue(periodicWorkRequest)

        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
            .observe(this, androidx.lifecycle.Observer {
                if(it.state.isFinished){
                    val data = it.outputData
                    val message = data.getString(KEY_WORKER)
                    Toast.makeText(context,message,Toast.LENGTH_LONG).show()
                }
            })
    }


}

package com.prathik.ecom.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.prathik.ecom.R
import com.prathik.ecom.SetLocation
import com.prathik.ecom.adapter.FoodItemAdapter
import com.prathik.ecom.adapter.HomeSliderAdapter
import com.prathik.ecom.adapter.model.SliderItem
import com.prathik.ecom.network.ApiClient
import com.prathik.ecom.network.models.cart.CartItem
import com.prathik.ecom.network.models.cart.GetCartModel
import com.prathik.ecom.network.models.foods.FoodModelOld
import com.prathik.ecom.network.models.getuser.GetUser
import com.prathik.ecom.network.models.products.ProductDetailsItem
import com.prathik.ecom.network.models.products.ProductModels
import com.prathik.ecom.utils.PreferenceManager
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController.ClickListener
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(),FoodItemAdapter.OnProuctAddedListener {


    private var viewScrolled = 0f
    private val TAG="HomeFragment12"

    private lateinit var foodAdapter: FoodItemAdapter
    private lateinit var modelArrayList: ArrayList<ProductDetailsItem>

    private fun init() {

        modelArrayList = ArrayList()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        homeRecyclerView.layoutManager = layoutManager
        homeRecyclerView.itemAnimator = DefaultItemAnimator()
        foodAdapter = FoodItemAdapter(activity as Context, modelArrayList,this)
        homeRecyclerView?.adapter = foodAdapter

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       var view = inflater.inflate(R.layout.fragment_home, container, false)




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setNameLocation()
        setAddressClick()
        setSlider()
        setNestedScrollViewListener()
        getProductsfromAPI()

    }

    private fun setNameLocation(){
        if(PreferenceManager.getString(PreferenceManager.USER_NAME,"")==""){
            hiNameTv.text="Hi Guest"
        }else{
            hiNameTv.text="Hi ${PreferenceManager.getString(PreferenceManager.USER_NAME,"")}"
        }
    }

    private fun setNestedScrollViewListener(){

           homeNestedScrollView.viewTreeObserver.addOnScrollChangedListener {
               try {
                   if (viewScrolled < homeNestedScrollView.scrollY) {
                       viewScrolled = homeNestedScrollView.scrollY.toFloat()
                       Log.d(TAG, "scrolling up");
                       EventBus.getDefault().post("UP") //post event

                   } else if (viewScrolled > homeNestedScrollView.scrollY) {
                       viewScrolled = homeNestedScrollView.scrollY.toFloat()
                       Log.d(TAG, "scrolling down")
                       EventBus.getDefault().post("DOWN") //post event
                   }
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


    private fun getProductsfromAPI(){
        homeShimmer.startShimmer()
        val call: Call<ProductModels> = ApiClient.getClient.getAllProducts()
        call.enqueue(object : Callback<ProductModels> {
            override fun onFailure(call: Call<ProductModels>, t: Throwable) {
               Log.d(TAG,"API Error: ${t.message}")
                homeShimmer.stopShimmer()
            }

            override fun onResponse(call: Call<ProductModels>, response: Response<ProductModels>) {
                homeShimmer.stopShimmer()

                Log.d(TAG,"URL :${response.raw().request().url()}")
                val successResponse: String =  Gson().toJson(response.body())
                Log.d(TAG, "successResponse: $successResponse")

                var products: ProductModels? = response.body()

                modelArrayList?.addAll(products?.productDetails as Collection<ProductDetailsItem>)
                foodAdapter.notifyDataSetChanged()
            }

        })

    }

    override fun onProductAdded(count: Int, position: Int) {
        if(count==0){
            removeFromcartAPI(modelArrayList[position].Id)
        }else{
            addToCartAPI(modelArrayList[position].Id,count)
        }
    }


}

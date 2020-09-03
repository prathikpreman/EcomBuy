package com.prathik.ecomshopkeeper.network

import com.prathik.ecomshopkeeper.network.models.appsettings.GetAppSettings
import com.prathik.ecomshopkeeper.network.models.cart.CartItem
import com.prathik.ecomshopkeeper.network.models.cart.GetCartModel
import com.prathik.ecomshopkeeper.network.models.category.GetAllCategory
import com.prathik.ecomshopkeeper.network.models.getuser.GetUser
import com.prathik.ecomshopkeeper.network.models.products.ProductModels
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {

    @POST("users/getuser")
    fun getUserbyNumber(@Query("mobileNumber") phoneNumber:String): Call<GetUser>

    @POST("users/register")
    fun registerNewUser(@Query("userName") userName:String,@Query("mobileNumber") mobileNumber:String,@Query("password") password:String): Call<GetUser>

    @GET("products")
    fun getAllProducts(): Call<ProductModels>

    @POST("cart/addtoCart")
    fun addToCart(@Query("user_id") userId:String,
                    @Query("product_id") productId:String,
                    @Query("count") count:Int) : Call<CartItem>

    @POST("cart/removeFromCart")
    fun removeFromCart(@Query("user_id") userId:String,
                  @Query("product_id") productId:String): Call<GetCartModel>


    // CATEGORY

    @GET("category/getAllCategories")
    fun getAllCategory(): Call<GetAllCategory>

    // APP SETTINGS

    @GET("appSettings/get")
    fun getAppSettings(): Call<GetAppSettings>


//    @POST("cart/arrayCart")
//    fun addToCartTemp(): Call<>

}
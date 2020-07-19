package com.prathik.ecom.network

import com.prathik.ecom.network.models.getuser.GetUser
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {

    @POST("users/getuser")
    fun getUserbyNumber(@Query("mobileNumber") phoneNumber:String): Call<GetUser>

    @POST("users/register")
    fun registerNewUser(@Query("userName") userName:String,@Query("mobileNumber") mobileNumber:String,@Query("password") password:String): Call<GetUser>
}
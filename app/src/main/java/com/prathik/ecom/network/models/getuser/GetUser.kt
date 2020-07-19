package com.prathik.ecom.network.models.getuser


import com.google.gson.annotations.SerializedName

data class GetUser(@SerializedName("message")
                    val message: String? = "",
                   @SerializedName("user")
                    val user:  UserItem?,
                   @SerializedName("status")
                    val status: Int? = 0)


data class UserItem(@SerializedName("password")
                    val password: String? = "",
                    @SerializedName("mobileNumber")
                    val mobileNumber: String? = "",
                    @SerializedName("__v")
                    val V: Int? = 0,
                    @SerializedName("_id")
                    val Id: String? = "",
                    @SerializedName("userName")
                    val userName: String? = "")



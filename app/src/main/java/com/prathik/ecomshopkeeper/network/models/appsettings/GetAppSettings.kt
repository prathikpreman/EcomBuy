package com.prathik.ecomshopkeeper.network.models.appsettings


import com.google.gson.annotations.SerializedName

data class AppSettings(@SerializedName("maximumCart")
                       val maximumCart: Int = 0,
                       @SerializedName("isCouponRequired")
                       val isCouponRequired: Boolean = false,
                       @SerializedName("__v")
                       val V: Int = 0,
                       @SerializedName("deliveryCharge")
                       val deliveryCharge: Float = 0.0F,
                       @SerializedName("tax")
                       val tax: Float = 0.0F,
                       @SerializedName("_id")
                       val Id: String = "")


data class GetAppSettings(@SerializedName("appSettings")
                          val appSettings: AppSettings,
                          @SerializedName("message")
                          val message: String = "",
                          @SerializedName("status")
                          val status: Int = 0)



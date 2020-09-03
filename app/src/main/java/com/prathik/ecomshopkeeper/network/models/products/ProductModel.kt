package com.prathik.ecomshopkeeper.network.models.products


import com.google.gson.annotations.SerializedName

data class ProductDetailsItem( @SerializedName("offerValue")
                               val offerValue: Float = 0F,
                              @SerializedName("foodName")
                              val foodName: String = "",
                              @SerializedName("offerPrice")
                              val offerPrice: Float = 0F,
                              @SerializedName("reviewCount")
                              val reviewCount: Int = 0,
                              @SerializedName("price")
                              val price: Float = 0F,
                              @SerializedName("imageUrl")
                              val imageUrl: String = "",
                              @SerializedName("__v")
                              val V: Int = 0,
                              @SerializedName("_id")
                              val Id: String = "",
                              @SerializedName("categoryId")
                              val categoryId: String = "",
                               @SerializedName("offerType")
                               val offerType: Int = 0,
                               @SerializedName("foodDescription")
                               val foodDescription: String = "",
                               @SerializedName("count")
                               val count:Int=0)



data class ProductModels(@SerializedName("message")
                         val message: String = "",
                         @SerializedName("productDetails")
                         val productDetails: ArrayList<ProductDetailsItem>?,
                         @SerializedName("status")
                         val status: Int = 0)



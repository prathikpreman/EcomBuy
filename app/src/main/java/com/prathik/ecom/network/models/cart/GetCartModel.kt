package com.prathik.ecom.network.models.cart


import com.google.gson.annotations.SerializedName
import com.prathik.ecom.network.models.products.ProductDetailsItem


data class GetCartModel(@SerializedName("count")
                        val count: Int = 0,
                        @SerializedName("message")
                        val message: String = "",
                        @SerializedName("cart")
                        val cart: List<CartItem>?,
                        @SerializedName("status")
                        val status: Int = 0)


data class CartItem(@SerializedName("count")
                    val count: Int = 0,
                    @SerializedName("productDetails")
                    val productDetails: ProductDetailsItem
)



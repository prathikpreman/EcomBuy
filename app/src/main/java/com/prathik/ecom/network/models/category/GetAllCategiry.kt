package com.prathik.ecom.network.models.category


import com.google.gson.annotations.SerializedName

data class GetAllCategory(@SerializedName("categoriesDetails")
                          val categoriesDetails: List<CategoriesDetailsItem>?,
                          @SerializedName("message")
                          val message: String = "",
                          @SerializedName("status")
                          val status: Int = 0)


data class CategoriesDetailsItem(@SerializedName("category_image")
                                 val categoryImage: String = "",
                                 @SerializedName("category_name")
                                 val categoryName: String = "",
                                 @SerializedName("category_id")
                                 val categoryId: String = "",
                                 @SerializedName("__v")
                                 val V: Int = 0,
                                 @SerializedName("category_desc")
                                 val categoryDesc: String = "",
                                 @SerializedName("_id")
                                 val Id: String = "")



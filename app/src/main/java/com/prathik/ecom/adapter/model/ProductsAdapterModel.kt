package com.prathik.ecom.adapter.model

import io.realm.RealmObject

open class ProductsAdapterModel(
    var offerValue: Float = 0F,
    var foodName: String = "",
    var offerPrice: Float = 0F,
    var reviewCount: Int = 0,
    var price: Float = 0F,
    var imageUrl: String = "",
    var V: Int = 0,
    var Id: String = "",
    var categoryId: Int = 0,
    var offerType: Int = 0,
    var foodDescription: String = "",
    var count:Int=0
)
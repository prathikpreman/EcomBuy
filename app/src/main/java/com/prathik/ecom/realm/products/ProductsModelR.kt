package com.prathik.ecom.realm.products

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.jetbrains.annotations.NotNull

open class ProductsModelR(
    @PrimaryKey var _id:String="",
    var offerValue: Float = 0F,
    var foodName: String = "",
    var offerPrice: Float = 0F,
    var reviewCount: Int = 0,
    var price: Float = 0F,
    var imageUrl: String = "",
    var V: Int = 0,
    var isBeingSaved:Boolean=false,
    var product_Id: String = "",
    var categoryId: String = "",
    var offerType: Int = 0,
    var foodDescription: String = "",
    var count:Int=0
):RealmObject()
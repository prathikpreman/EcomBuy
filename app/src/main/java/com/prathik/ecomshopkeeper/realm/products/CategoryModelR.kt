package com.prathik.ecomshopkeeper.realm.products

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CategoryModelR(
    @PrimaryKey
    var _id: String = "",
    var categoryName: String = "",
    var categoryId: String = "",
    var categoryImageUrl:String="",
    var categoryDescription:String=""
): RealmObject()



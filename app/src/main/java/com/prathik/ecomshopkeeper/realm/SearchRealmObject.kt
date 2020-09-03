package com.prathik.ecomshopkeeper.realm

import com.prathik.ecomshopkeeper.realm.products.ProductsModelR
import io.realm.RealmResults

object SearchRealmObject {


    fun searchProduct(keyword:String?): RealmResults<ProductsModelR> {
        GetRealmInstance.instance.beginTransaction()
        var query = GetRealmInstance.instance.where(ProductsModelR::class.java).contains("foodName",keyword).findAll()
        GetRealmInstance.instance.commitTransaction()
        return query
    }
}
package com.prathik.ecom.realm

import com.prathik.ecom.realm.products.ProductsModelR
import io.realm.RealmResults

object SearchRealmObject {


    fun searchProduct(keyword:String?): RealmResults<ProductsModelR> {
        GetRealmInstance.instance.beginTransaction()
        var query = GetRealmInstance.instance.where(ProductsModelR::class.java).contains("foodName",keyword).findAll()
        GetRealmInstance.instance.commitTransaction()
        return query
    }
}
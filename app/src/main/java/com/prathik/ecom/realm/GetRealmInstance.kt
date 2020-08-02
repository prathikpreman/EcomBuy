package com.prathik.ecom.realm

import com.prathik.ecom.network.models.products.ProductModels
import com.prathik.ecom.realm.products.ProductsModelR
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

object GetRealmInstance {
    val instance: Realm = Realm.getDefaultInstance()


    fun close(){
        instance.close()
    }


}
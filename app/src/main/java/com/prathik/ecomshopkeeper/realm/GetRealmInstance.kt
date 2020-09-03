package com.prathik.ecomshopkeeper.realm

import io.realm.Realm

object GetRealmInstance {
    val instance: Realm = Realm.getDefaultInstance()


    fun close(){
        instance.close()
    }


}
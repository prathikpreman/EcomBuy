package com.prathik.ecomshopkeeper.realm

import com.prathik.ecomshopkeeper.realm.products.ProductsModelR
import io.realm.RealmChangeListener
import io.realm.RealmResults

object CartRealmObject {



    fun getCartTotal():Float{
        var total=0F
        GetRealmInstance.instance.beginTransaction()
        var query= GetRealmInstance.instance.where(ProductsModelR::class.java).greaterThan("count",0).findAll()
        GetRealmInstance.instance.commitTransaction()
        if(query==null){
            return total
        }
        for (obj in query){
            total += obj.count * obj.price
        }



        query.addChangeListener(RealmChangeListener { person ->
            // React to change
        })

        return total
    }


    lateinit var entries: RealmResults<ProductsModelR>

    fun getCartTotalListener(onCartTotalChangedListener: OnCartTotalChangedListener){

        GetRealmInstance.instance.beginTransaction()
        entries= GetRealmInstance.instance.where(ProductsModelR::class.java).greaterThan("count",0).findAll()
        GetRealmInstance.instance.commitTransaction()
        entries.addChangeListener(RealmChangeListener { person ->
            var total=0F
            if(entries!=null){
                for (obj in entries){
                    total += obj.count * obj.price
                }
            }
            onCartTotalChangedListener.onCartTotalChanged(total)
        })

    }

    fun removeAllProductListeners(){
        entries.removeAllChangeListeners()
    }


    fun getCartSizeListener(onCartSizeChangedListener: OnCartSizeChangedListener) {
        GetRealmInstance.instance.beginTransaction()
        entries = GetRealmInstance.instance.where(ProductsModelR::class.java).greaterThan("count", 0).findAll()
        GetRealmInstance.instance.commitTransaction()
        entries.addChangeListener(RealmChangeListener{ size->
            onCartSizeChangedListener.onCartSizeChanged(entries.size)
        })
    }

    fun close(){
        GetRealmInstance.close()
    }


    interface OnCartTotalChangedListener{
        fun onCartTotalChanged(total:Float)
    }

    interface OnCartSizeChangedListener{
        fun onCartSizeChanged(size:Int)
    }

}
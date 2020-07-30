package com.prathik.ecom.realm

import com.prathik.ecom.R
import com.prathik.ecom.network.models.products.ProductModels
import com.prathik.ecom.realm.products.ProductsModelR
import io.realm.RealmChangeListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_cart.*

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


    fun getCartTotalListener(onCartTotalChangedListener: OnCartTotalChangedListener){

        GetRealmInstance.instance.beginTransaction()
        val query= GetRealmInstance.instance.where(ProductsModelR::class.java).greaterThan("count",0).findAll()
        GetRealmInstance.instance.commitTransaction()
        query.addChangeListener(RealmChangeListener { person ->
            var total=0F
            if(query!=null){
                for (obj in query){
                    total += obj.count * obj.price
                }
            }
            onCartTotalChangedListener.onCartTotalChanged(total)
        })


    }


    interface OnCartTotalChangedListener{
        fun onCartTotalChanged(total:Float)
    }

}
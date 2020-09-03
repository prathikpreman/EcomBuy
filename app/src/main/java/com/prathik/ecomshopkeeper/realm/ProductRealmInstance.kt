package com.prathik.ecomshopkeeper.realm

import com.prathik.ecomshopkeeper.network.models.products.ProductModels
import com.prathik.ecomshopkeeper.realm.products.ProductsModelR
import io.realm.RealmResults
import java.util.*

object ProductRealmInstance {

    fun getAllProducts(): RealmResults<ProductsModelR> {
        GetRealmInstance.instance.beginTransaction()
        var query = GetRealmInstance.instance.where(ProductsModelR::class.java).findAll()
        GetRealmInstance.instance.commitTransaction()
        //  query.equalTo("Id", "5f16a7d1945c67266583582e")
        // query.or().equalTo("name", "Peter")

        return query
    }




    fun getAllProductsByCatId(catId:String?): RealmResults<ProductsModelR> {
        GetRealmInstance.instance.beginTransaction()
        var query = GetRealmInstance.instance.where(ProductsModelR::class.java).equalTo("categoryId",catId).findAll()
        GetRealmInstance.instance.commitTransaction()
        return query
    }

    fun addToProduct(products: ProductModels?) {
        GetRealmInstance.instance.beginTransaction()
        for (obj in products?.productDetails.orEmpty()) {
            val editProduct: ProductsModelR? =
                GetRealmInstance.instance.where(ProductsModelR::class.java).equalTo("product_Id", obj.Id).findFirst()
            if (editProduct != null) {
                editProduct.categoryId = obj.categoryId
                editProduct.foodDescription = obj.foodDescription
                editProduct.foodName = obj.foodName
                editProduct.imageUrl = obj.imageUrl
                editProduct.offerPrice = obj.offerPrice
                editProduct.offerValue = obj.offerValue
                editProduct.offerType = obj.offerType
                editProduct.price = obj.price
                editProduct.reviewCount = obj.reviewCount
                editProduct.isBeingSaved = true
                GetRealmInstance.instance.insertOrUpdate(editProduct)

            } else {

                val parameter: ProductsModelR =
                    GetRealmInstance.instance.createObject(ProductsModelR::class.java, UUID.randomUUID().toString())
                parameter.product_Id = obj.Id
                parameter.categoryId = obj.categoryId
                parameter.count = obj.count
                parameter.foodDescription = obj.foodDescription
                parameter.foodName = obj.foodName
                parameter.imageUrl = obj.imageUrl
                parameter.offerPrice = obj.offerPrice
                parameter.offerValue = obj.offerValue
                parameter.offerType = obj.offerType
                parameter.price = obj.price
                parameter.reviewCount = obj.reviewCount
                parameter.isBeingSaved = true
                GetRealmInstance.instance.insert(parameter)
            }
        }

        GetRealmInstance.instance.where(ProductsModelR::class.java).equalTo("isBeingSaved", false).findAll()
            .deleteAllFromRealm()

        for (updateBeingSaved in GetRealmInstance.instance.where(ProductsModelR::class.java).findAll()) {
            updateBeingSaved.isBeingSaved = false
        }

        GetRealmInstance.instance.commitTransaction()
    }

    fun deleteAllProducts() {
        GetRealmInstance.instance.beginTransaction()
        GetRealmInstance.instance.where(ProductsModelR::class.java).findAll().deleteAllFromRealm()
        GetRealmInstance.instance.commitTransaction()
    }

    fun getProductSize(): Int {
        GetRealmInstance.instance.beginTransaction()
        val query = GetRealmInstance.instance.where(ProductsModelR::class.java).findAll().size
        //  query.equalTo("Id", "5f16a7d1945c67266583582e")
        // query.or().equalTo("name", "Peter")
        GetRealmInstance.instance.commitTransaction()
        return query
    }

    fun isProductAvaiableinRealm(): Boolean {
        if (getProductSize() > 0) {
            return true
        }
        return false
    }


    fun isCartItemAvaliable(): Boolean {
        GetRealmInstance.instance.beginTransaction()
        val query = GetRealmInstance.instance.where(ProductsModelR::class.java).greaterThan("count", 0).findAll().size
        GetRealmInstance.instance.commitTransaction()
        if (query > 0) {
            return true
        }

        return false
    }

    fun getCartSize(): Int {
        GetRealmInstance.instance.beginTransaction()
        val query = GetRealmInstance.instance.where(ProductsModelR::class.java).greaterThan("count", 0).findAll().size
        GetRealmInstance.instance.commitTransaction()
        return query
    }


    fun updateCartByProductId(id: String, newCount: Int) {
        GetRealmInstance.instance.beginTransaction()
        if (newCount >= 0) {
            val editCart: ProductsModelR? =
                GetRealmInstance.instance.where(ProductsModelR::class.java).equalTo("product_Id", id).findFirst()
            editCart?.count = newCount
            GetRealmInstance.instance.insertOrUpdate(editCart)
        }
        GetRealmInstance.instance.commitTransaction()
    }
}
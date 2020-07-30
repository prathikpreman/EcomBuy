package com.prathik.ecom.realm

import com.prathik.ecom.network.models.category.CategoriesDetailsItem
import com.prathik.ecom.network.models.products.ProductModels
import com.prathik.ecom.realm.products.CategoryModelR
import com.prathik.ecom.realm.products.ProductsModelR
import io.realm.RealmResults
import java.util.*

object CategoryRealmInstance {

    fun getAllCategory(): RealmResults<CategoryModelR> {
        GetRealmInstance.instance.beginTransaction()
        var query = GetRealmInstance.instance.where(CategoryModelR::class.java).findAll()
        GetRealmInstance.instance.commitTransaction()
        //  query.equalTo("Id", "5f16a7d1945c67266583582e")
        // query.or().equalTo("name", "Peter")
        return query
    }


    fun addCategory(categories: List<CategoriesDetailsItem>) {
        deleteAllCategory()
        GetRealmInstance.instance.beginTransaction()
        for (item in categories) {
            val parameter: CategoryModelR =
                GetRealmInstance.instance.createObject(CategoryModelR::class.java, UUID.randomUUID().toString())
            parameter.categoryId = item.categoryId
            parameter.categoryName = item.categoryName
            parameter.categoryDescription = item.categoryDesc
            parameter.categoryImageUrl = item.categoryImage
            GetRealmInstance.instance.insert(parameter)
        }

        GetRealmInstance.instance.commitTransaction()
    }

    fun deleteAllCategory(){
        GetRealmInstance.instance.beginTransaction()
        GetRealmInstance.instance.where(CategoryModelR::class.java).findAll().deleteAllFromRealm()
        GetRealmInstance.instance.commitTransaction()
    }

    fun getCategoryNamebyId(categoryId:String?):String{
        GetRealmInstance.instance.beginTransaction()
        var query=GetRealmInstance.instance.where(CategoryModelR::class.java).equalTo("categoryId",categoryId).findFirst()
        GetRealmInstance.instance.commitTransaction()
        if(query!=null){
            return query.categoryName
        }
        return ""
    }


}
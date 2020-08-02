package com.prathik.ecom

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prathik.ecom.adapter.CategoryAdapter
import com.prathik.ecom.adapter.FoodItemAdapter
import com.prathik.ecom.realm.CategoryRealmInstance
import com.prathik.ecom.realm.ProductRealmInstance
import com.prathik.ecom.realm.products.ProductsModelR
import com.prathik.ecom.utils.AppConstants
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_category_products.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.main_toolbar.*

class CategoryProducts : AppCompatActivity(), FoodItemAdapter.OnProuctAddedListener {

    var categoryId:String?="0"
    private lateinit var foodAdapter: FoodItemAdapter
    private  var modelArrayList: RealmResults<ProductsModelR>?= null

    private fun init(){

        toolbarSearch.visibility=View.GONE
        categoryId=intent.getStringExtra(AppConstants.CATEGORY_ID)
        modelArrayList  = ProductRealmInstance.getAllProductsByCatId(categoryId)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        foodAdapter = FoodItemAdapter(this, modelArrayList,this)
        catRecyclerView?.adapter = foodAdapter

        catRecyclerView.layoutManager = layoutManager
        catRecyclerView.itemAnimator = DefaultItemAnimator()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_products)
        init()
        setToolbar()

        toolbarBackBtn.setOnClickListener {
            finish()
        }


    }



    private fun setToolbar(){
        supportActionBar?.hide()
        homeToolbarPad.visibility= View.VISIBLE
        toolbarText.text=CategoryRealmInstance.getCategoryNamebyId(categoryId)
    }

    override fun onProductAdded(count: Int, productId: String) {
      //  val id= modelArrayList?.get(position)?.product_Id
            ProductRealmInstance.updateCartByProductId(productId,count)

    }
}

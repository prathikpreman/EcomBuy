package com.prathik.ecomshopkeeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prathik.ecomshopkeeper.adapter.FoodItemAdapter
import com.prathik.ecomshopkeeper.realm.CategoryRealmInstance
import com.prathik.ecomshopkeeper.realm.ProductRealmInstance
import com.prathik.ecomshopkeeper.realm.products.ProductsModelR
import com.prathik.ecomshopkeeper.utils.AppConstants
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_category_products.*
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

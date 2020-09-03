package com.prathik.ecomshopkeeper

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prathik.ecomshopkeeper.adapter.SearchItemAdapter
import com.prathik.ecomshopkeeper.realm.ProductRealmInstance
import com.prathik.ecomshopkeeper.realm.SearchRealmObject
import com.prathik.ecomshopkeeper.realm.products.ProductsModelR
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*


class SearchActivity : AppCompatActivity(), SearchItemAdapter.OnProuctAddedListener {


    val TAG="SearchActivity45"
    private lateinit var searchAdapter: SearchItemAdapter
    lateinit  var modelArrayList: RealmResults<ProductsModelR>

    private fun init(){

        modelArrayList  = SearchRealmObject.searchProduct("")
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        searchAdapter = SearchItemAdapter(this, modelArrayList,this)
        searchRecyclerView?.adapter = searchAdapter
        searchAdapter.notifyDataSetChanged()

        searchRecyclerView.layoutManager = layoutManager
        searchRecyclerView.itemAnimator = DefaultItemAnimator()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar?.hide()
        init()

        searchBox.requestFocus()

      //  addBeef()
        searchBox.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               // modelArrayList  = SearchRealmObject.searchProduct("Beef")
                if(s.toString().length>2){
                    searchAdapter.filter.filter(s.toString())
                }

              //  searchAdapter.notifyDataSetChanged()
            }

        })
    }

    override fun onResume() {
        super.onResume()

        val tt: TimerTask = object : TimerTask() {
            override fun run() {
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT)
            }

        }

        val timer = Timer()
        timer.schedule(tt, 200)


    }

    override fun onProductAdded(count: Int,productId: String) {
            ProductRealmInstance.updateCartByProductId(productId,count)
            Log.d(TAG," Cart Updated...!")
    }



}


//Log.d(TAG,"text $s")
//modelArrayList  = SearchRealmObject.searchProduct(s.toString())
//searchAdapter = SearchItemAdapter(this@SearchActivity, modelArrayList,this@SearchActivity)
//
//searchAdapter.
//
//searchAdapter.notifyDataSetChanged()
//if(modelArrayList.size>0){
//    for (obj in modelArrayList){
//        Log.d("foodName35",": ${obj.foodName}")
//    }
//
//}
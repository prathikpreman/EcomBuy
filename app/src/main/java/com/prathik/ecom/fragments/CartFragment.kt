package com.prathik.ecom.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.prathik.ecom.R
import com.prathik.ecom.adapter.CartAdapter
import com.prathik.ecom.realm.CartRealmObject
import com.prathik.ecom.realm.GetRealmInstance
import com.prathik.ecom.realm.ProductRealmInstance
import com.prathik.ecom.realm.products.ProductsModelR
import io.realm.RealmChangeListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_cart.*

/**
 * A simple [Fragment] subclass.
 */
class CartFragment : Fragment(),CartAdapter.OnProuctAddedListener {

    private val TAG="HomeFragment12"

    private lateinit var cartAdapter: CartAdapter
    private  var modelArrayList: RealmResults<ProductsModelR>?= null


    private fun init() {

        modelArrayList = ProductRealmInstance.getAllProducts()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        cartAdapter = CartAdapter(activity as Context, modelArrayList,this)
        cartRecyclerView?.adapter = cartAdapter

        cartRecyclerView.layoutManager = layoutManager
        cartRecyclerView.itemAnimator = DefaultItemAnimator()

        cart_total_tv.text="Cart Total: ${getString(R.string.rupee)} ${CartRealmObject.getCartTotal()}"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()


        if(ProductRealmInstance.isCartItemAvaliable()){

            modelArrayList=ProductRealmInstance.getAllProducts()

        }


        CartRealmObject.getCartTotalListener(object :CartRealmObject.OnCartTotalChangedListener{
            override fun onCartTotalChanged(total: Float) {
                Log.d(TAG,"Realm Total changed!!!!  :  $total")
                if(activity!=null){
                   cart_total_tv.text="Cart Total: ${getString(R.string.rupee)} $total"
                }
            }

        })

    }

    override fun onProductAdded(count: Int, position: Int) {
       val id= modelArrayList?.get(position)?.product_Id
        if(id!=null && modelArrayList != null){
            ProductRealmInstance.updateCartByProductId(id,count)
        }
    }




}

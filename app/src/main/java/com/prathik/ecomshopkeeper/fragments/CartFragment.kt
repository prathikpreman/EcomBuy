package com.prathik.ecomshopkeeper.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.prathik.ecomshopkeeper.R
import com.prathik.ecomshopkeeper.ReviewActivity
import com.prathik.ecomshopkeeper.adapter.CartAdapter
import com.prathik.ecomshopkeeper.realm.CartRealmObject
import com.prathik.ecomshopkeeper.realm.ProductRealmInstance
import com.prathik.ecomshopkeeper.realm.products.ProductsModelR
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_cart.*

/**
 * A simple [Fragment] subclass.
 */
class CartFragment : Fragment(), CartAdapter.OnProuctAddedListener {

    private val TAG = "HomeFragment12"

    private lateinit var cartAdapter: CartAdapter
    private var modelArrayList: RealmResults<ProductsModelR>? = null
    lateinit var cartRealmObject:CartRealmObject


    private fun init() {
        cartRealmObject=CartRealmObject
        modelArrayList = ProductRealmInstance.getAllProducts()
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        cartAdapter = CartAdapter(activity as Context, modelArrayList, this)
        cartRecyclerView?.adapter = cartAdapter

        cartRecyclerView.layoutManager = layoutManager
        cartRecyclerView.itemAnimator = DefaultItemAnimator()

        cart_total_tv.text =
            "Cart Total: ${getString(R.string.rupee)} ${CartRealmObject.getCartTotal()}"

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


        if (ProductRealmInstance.isCartItemAvaliable()) {
            modelArrayList = ProductRealmInstance.getAllProducts()
        } else {
            cart_empty_layout.visibility + View.VISIBLE
            cart_layout.visibility = View.GONE
        }


        cartRealmObject.getCartTotalListener(object : CartRealmObject.OnCartTotalChangedListener {
            override fun onCartTotalChanged(total: Float) {
                Log.d(TAG, "Realm Total changed!!!!  :  $total")
                if (activity != null && total > 0.0F) {
                    cart_empty_layout.visibility + View.GONE
                    cart_layout.visibility = View.VISIBLE
                    cart_total_tv.text = "Cart Total: ${getString(R.string.rupee)} $total"
                } else if (activity != null && total == 0.0F) {
                    cart_empty_layout.visibility + View.VISIBLE
                    cart_layout.visibility = View.GONE
                }
            }

        })









        placeOrderBtn.setOnClickListener {
            startActivity(Intent(context, ReviewActivity::class.java));
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG," Lifecycle::: Destroyed")
        cartRealmObject.removeAllProductListeners()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG," Lifecycle::: Detach")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG," Lifecycle::: Pause")
    }

    override fun onProductAdded(count: Int, position: Int) {
        val id = modelArrayList?.get(position)?.product_Id
        if (id != null && modelArrayList != null) {
            ProductRealmInstance.updateCartByProductId(id, count)
        }
    }


}

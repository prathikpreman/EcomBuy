package com.prathik.ecomshopkeeper.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prathik.ecomshopkeeper.R
import com.prathik.ecomshopkeeper.customView.AddButtonView
import com.prathik.ecomshopkeeper.realm.products.ProductsModelR
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

class CartAdapter public constructor(
    context: Context,
    var modelArrayList: RealmResults<ProductsModelR>?,
    onProductAddedListener: OnProuctAddedListener
) : RealmRecyclerViewAdapter<ProductsModelR, CartAdapter.ViewHolder>(modelArrayList,true) {

    var contexts:Context?= context
    var onProductAddedListener:OnProuctAddedListener?= onProductAddedListener


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_fooditem_layout, parent, false)
        return ViewHolder(view)
    }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val obj= data?.get(position)

       if(obj!=null && obj.count > 0){

           holder.itemView.visibility=View.VISIBLE
           holder.itemView.layoutParams = RecyclerView.LayoutParams(
               ViewGroup.LayoutParams.MATCH_PARENT,
               ViewGroup.LayoutParams.WRAP_CONTENT
           )


           holder.foodname.text = obj?.foodName


           // holder.Date_Id.text = Date_Id
           holder.foodPrice.text = contexts?.getString(R.string.rupee)+" "+obj?.price.toString()

           Log.d("Adapter123","Refreshed!!!")

           if(obj?.foodDescription=="") {
               holder.foodDesc.visibility=View.GONE
           }else{
               holder.foodDesc.text = obj?.foodDescription
           }

           holder.foodAddbtnView.setCountFromAdapter(obj?.count)

           holder.cart_countPrice.text=getPerPrice(obj.price,obj.count)

           holder.foodAddbtnView.isCancelDialogRequired=true
           holder.foodAddbtnView.setCountListener(object :AddButtonView.OnCountChangedListener{
               override fun onCartCountUpdated(count: Int) {

                   onProductAddedListener?.onProductAdded(count,position)
                   holder.cart_countPrice.text=getPerPrice(obj.price,obj.count)
               }

           })
       }else{
           holder.itemView.visibility=View.GONE
          holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
       }

    }


    fun getPerPrice(price: Float,count:Int):String{

        return "$count x ${contexts?.getString(R.string.rupee)}$price = ${contexts?.getString(R.string.rupee)}${count*price}"
    }

    override fun getItemCount(): Int {
        var size=0
        return if(data==null){
            0
        }else{
            data?.size!!
        }


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var foodname: TextView = itemView.findViewById(R.id.Restaurant_Id)
        var foodDesc: TextView = itemView.findViewById(R.id.Address_Id)
        var foodPrice: TextView = itemView.findViewById(R.id.Dollar_Id)
        var foodAddbtnView: AddButtonView = itemView.findViewById(R.id.foodAddbtnView)
        var foodCardParentLayout: LinearLayout = itemView.findViewById(R.id.foodCard)
        var cart_countPrice: TextView = itemView.findViewById(R.id.cart_countPrice)

    }

   


    interface OnProuctAddedListener{
        fun onProductAdded(count:Int, position: Int)
    }



}
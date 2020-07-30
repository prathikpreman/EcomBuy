package com.prathik.ecom.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prathik.ecom.R
import com.prathik.ecom.customView.AddButtonView
import com.prathik.ecom.realm.products.ProductsModelR
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

class CartAdapter public constructor(
    context: Context,
    var modelArrayList: RealmResults<ProductsModelR>?,
    onProductAddedListener: OnProuctAddedListener
) : RealmRecyclerViewAdapter<ProductsModelR, CartAdapter.ViewHolder>(context,modelArrayList,true) {

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
       val obj= modelArrayList?.get(position)

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

        return "$count x ${contexts?.getString(R.string.rupee)} $price = ${count*price}"
    }

    override fun getItemCount(): Int {
        var size=0
        return if(modelArrayList==null){
            0
        }else{
            modelArrayList?.size!!
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

    fun getOfferSymbol( offerValue:Float?, offerType:Int?):String{

        when(offerType){

            1->{  // FLAT
                return  "${contexts?.getString(R.string.rupee)} $offerValue OFF"
            }
            2->{
                return "$offerValue% OFF"
            }
        }
        return ""
    }


    interface OnProuctAddedListener{
        fun onProductAdded(count:Int, position: Int)
    }



}
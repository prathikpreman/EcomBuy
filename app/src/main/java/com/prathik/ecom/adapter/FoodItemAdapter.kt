package com.prathik.ecom.adapter

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prathik.ecom.R
import com.prathik.ecom.adapter.model.ProductsAdapterModel
import com.prathik.ecom.customView.AddButtonView
import com.prathik.ecom.realm.products.ProductsModelR
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

class FoodItemAdapter public constructor(
    context: Context,
    var modelArrayList: RealmResults<ProductsModelR>?,
    onProductAddedListener: OnProuctAddedListener
) : RealmRecyclerViewAdapter<ProductsModelR, FoodItemAdapter.ViewHolder>(context,modelArrayList,true) {

    var contexts:Context?= context
    var onProductAddedListener:OnProuctAddedListener?= onProductAddedListener


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.food_items, parent, false)
        return ViewHolder(view)
    }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val obj= modelArrayList?.get(position)
       // val (offerValue,foodName, offerPrice, reviewCount, price, imageUrl, V , Id,categoryId,offerType,foodDesc,count) = modelArrayList[position]
        holder.foodname.text = obj?.foodName
        holder.reviewCount.text = obj?.reviewCount.toString()

        holder.foodOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
       // holder.Date_Id.text = Date_Id
        holder.foodPrice.text = contexts?.getString(R.string.rupee)+" "+obj?.price.toString()
       // holder.foodImageUrl.setImageResource(FoodImage_Id)
       contexts?.let { Glide.with(it).load(obj?.imageUrl).centerCrop().into(holder.foodImageUrl) };
      //  holder.History_Id.setImageResource(History_Id)

       Log.d("Adapter123","Refreshed!!!")

       if(obj?.foodDescription=="") {
           holder.foodDesc.visibility=View.GONE
       }else{
           holder.foodDesc.text = obj?.foodDescription
       }

       if(obj!=null && obj.offerPrice < obj.price){  // Offer available
           holder.offerTag.visibility=View.VISIBLE
           holder.offerTag.text=getOfferSymbol(obj?.offerValue,obj.offerType)
           holder.foodOldPrice.visibility=View.VISIBLE
           holder.foodOldPrice.text="${contexts?.getString(R.string.rupee)} ${obj.offerPrice}"
       }else{
           holder.offerTag.visibility=View.GONE
           holder.foodOldPrice.visibility=View.GONE
       }

       holder.foodAddbtnView.setCountFromAdapter(obj?.count)

       holder.foodAddbtnView.setCountListener(object :AddButtonView.OnCountChangedListener{
           override fun onCartCountUpdated(count: Int) {
             //  Toast.makeText(context,"changed in adapter $count",Toast.LENGTH_LONG).show()
               onProductAddedListener?.onProductAdded(count,position)
           }

       })


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
        var foodname: TextView
        var foodDesc: TextView
        var reviewCount: TextView
       // var Date_Id: TextView
        var foodPrice: TextView
        var foodOldPrice: TextView
        var offerTag: TextView
        var foodImageUrl: ImageView
        var History_Id: ImageView
        var foodAddbtnView: AddButtonView

        init {
            foodname = itemView.findViewById(R.id.Restaurant_Id)
            foodDesc = itemView.findViewById(R.id.Address_Id)
            reviewCount = itemView.findViewById(R.id.Reviews_Id)
           // Date_Id = itemView.findViewById(R.id.Date_Id)
            foodPrice = itemView.findViewById(R.id.Dollar_Id)
            foodOldPrice = itemView.findViewById(R.id.oldPrice)
            foodImageUrl = itemView.findViewById(R.id.FoodImage_Id)
            History_Id = itemView.findViewById(R.id.History_Id)
            offerTag = itemView.findViewById(R.id.offer_tag)
            foodAddbtnView = itemView.findViewById(R.id.foodAddbtnView)
        }
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
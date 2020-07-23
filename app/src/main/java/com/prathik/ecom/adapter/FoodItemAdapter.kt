package com.prathik.ecom.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prathik.ecom.R
import com.prathik.ecom.customView.AddButtonView
import com.prathik.ecom.network.models.products.ProductDetailsItem

class FoodItemAdapter(private val context: Context, private val modelArrayList: ArrayList<ProductDetailsItem>, onProductAddedListener:OnProuctAddedListener) :
    RecyclerView.Adapter<FoodItemAdapter.ViewHolder>() {

    var onProductAddedListener=onProductAddedListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.food_items, parent, false)
        return ViewHolder(view)
    }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (offerValue,foodName, offerPrice, reviewCount, price, imageUrl, V , Id,categoryId,offerType,foodDesc,count) = modelArrayList[position]
        holder.foodname.text = foodName
        holder.foodDesc.text = foodName
        holder.reviewCount.text = reviewCount.toString()

        holder.foodOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
       // holder.Date_Id.text = Date_Id
        holder.foodPrice.text = context.getString(R.string.rupee)+" "+price.toString()
       // holder.foodImageUrl.setImageResource(FoodImage_Id)
       Glide.with(context).load(imageUrl).centerCrop().into(holder.foodImageUrl);
      //  holder.History_Id.setImageResource(History_Id)

       if(foodDesc=="") {
           holder.foodDesc.visibility=View.GONE
       }else{
           holder.foodDesc.text = foodDesc
       }



       if(offerPrice<price){  // Offer available
           holder.offerTag.visibility=View.VISIBLE
           holder.offerTag.text=getOfferSymbol(offerValue,offerType)
           holder.foodOldPrice.visibility=View.VISIBLE
           holder.foodOldPrice.text="${context.getString(R.string.rupee)} $offerPrice"
       }else{
           holder.offerTag.visibility=View.GONE
           holder.foodOldPrice.visibility=View.GONE
       }

       holder.foodAddbtnView.setCount(count)

       holder.foodAddbtnView.setCountListener(object :AddButtonView.OnCountChangedListener{
           override fun onCartCountUpdated(count: Int) {
             //  Toast.makeText(context,"changed in adapter $count",Toast.LENGTH_LONG).show()
               onProductAddedListener.onProductAdded(count,position)
           }

       })


    }

    override fun getItemCount(): Int {
        return modelArrayList.size
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

    fun getOfferSymbol( offerValue:Float, offerType:Int):String{

        when(offerType){

            1->{  // FLAT
                return  "${context.getString(R.string.rupee)} $offerValue OFF"
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
package com.prathik.ecom.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.prathik.ecom.CategoryProducts
import com.prathik.ecom.R
import com.prathik.ecom.customView.AddButtonView
import com.prathik.ecom.realm.products.CategoryModelR
import com.prathik.ecom.realm.products.ProductsModelR
import com.prathik.ecom.utils.AppConstants
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

class CategoryAdapter constructor(
    context: Context,
    var categoryList: RealmResults<CategoryModelR>?)
    : RealmRecyclerViewAdapter<CategoryModelR, CategoryAdapter.ViewHolder>(categoryList,true) {

    var contexts:Context?= context


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.category_xml, parent, false)
        return ViewHolder(view)
    }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val obj= categoryList?.get(position)
       if(obj!=null){
           contexts?.let { Glide.with(it).load(obj.categoryImageUrl).centerCrop().into(holder.categoryImage) }
           holder.categoryName.text=obj.categoryName

           holder.categoryParentLayout.setOnClickListener {
               var intent=Intent(contexts,CategoryProducts::class.java)
               intent.putExtra(AppConstants.CATEGORY_ID,obj.categoryId)
               contexts?.startActivity(intent)
           }

       }
    }

    override fun getItemCount(): Int {
        return if(categoryList==null){
            0
        }else{
            categoryList?.size!!
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var categoryName: TextView = itemView.findViewById(R.id.categoryName)
        var categoryImage: ImageView = itemView.findViewById(R.id.categoryImage)
        var categoryParentLayout: LinearLayout = itemView.findViewById(R.id.categoryParentLayout)

    }




}
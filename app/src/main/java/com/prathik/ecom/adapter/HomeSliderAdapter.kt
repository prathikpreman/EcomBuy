package com.prathik.ecom.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.prathik.ecom.R
import com.prathik.ecom.adapter.model.SliderItem
import com.smarteist.autoimageslider.SliderViewAdapter


class HomeSliderAdapter(context: Context?) : SliderViewAdapter<HomeSliderAdapter.SliderAdapterVH>() {

    private var context: Context? = context
    private var mSliderItems: ArrayList<SliderItem> = ArrayList()


    fun renewItems(sliderItems: ArrayList<SliderItem>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: SliderItem) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    inner class SliderAdapterVH(itemView: View):SliderViewAdapter.ViewHolder(itemView) {

        var imageViewBackground: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)
         var imageGifContainer:ImageView = itemView.findViewById(R.id.iv_gif_container)
         var textViewDescription: TextView = itemView.findViewById(R.id.tv_auto_image_slider)
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val inflate: View =
            LayoutInflater.from(parent?.getContext()).inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {

        val sliderItem: SliderItem = mSliderItems.get(position)

        viewHolder!!.textViewDescription.text = sliderItem.description
        viewHolder!!.textViewDescription.textSize = 16f
        viewHolder!!.textViewDescription.setTextColor(Color.WHITE)
        Glide.with(viewHolder.itemView)
            .load(sliderItem.imageUrl)
            .fitCenter()
            .into(viewHolder!!.imageViewBackground)

        viewHolder.itemView.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                context,
                "This is item in position $position",
                Toast.LENGTH_SHORT
            ).show()
        })
    }
}
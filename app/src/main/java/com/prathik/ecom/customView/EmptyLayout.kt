package com.prathik.ecom.customView

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.prathik.ecom.R
import kotlinx.android.synthetic.main.empty_layout.view.*


class EmptyLayout:LinearLayout {



    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr){

        val a = context.obtainStyledAttributes(attrs, R.styleable.EmptyLayout, 0, 0)
        val titleText = ""+a.getString(R.styleable.EmptyLayout_titleText)
        val imageSrc = a.getResourceId(R.styleable.EmptyLayout_image,R.drawable.food)
        empty_layout_name.text=titleText
        Glide.with(this).load(imageSrc).into(empty_layout_image)
        a.recycle()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes){
        val a = context.obtainStyledAttributes(attrs, R.styleable.EmptyLayout, 0, 0)
        val titleText = ""+a.getString(R.styleable.EmptyLayout_titleText)
        val imageSrc = a.getResourceId(R.styleable.EmptyLayout_image,R.drawable.food)
        empty_layout_name.text=titleText
        Glide.with(this).load(imageSrc).into(empty_layout_image)
        a.recycle()
    }



    init {
        LayoutInflater.from(context).inflate(R.layout.empty_layout, this, true)
        orientation = VERTICAL






    }

    fun setTitle(title:String){
        empty_layout_name.text=title
    }


    fun setImageRes(imageSrc:Int){
         Glide.with(this).load(imageSrc).centerCrop().into(empty_layout_image)
    }




}



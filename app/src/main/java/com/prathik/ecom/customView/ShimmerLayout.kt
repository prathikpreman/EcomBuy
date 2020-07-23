package com.prathik.ecom.customView

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.prathik.ecom.R
import kotlinx.android.synthetic.main.customer_shimmer_layout.view.*
import kotlinx.android.synthetic.main.fragment_home.*

class ShimmerLayout : LinearLayout {
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(R.layout.customer_shimmer_layout, this, true)

        orientation = VERTICAL
    }

    fun startShimmer(){
        customShimmer.visibility=View.VISIBLE
        customShimmer.startShimmer()
    }

     fun stopShimmer(){
        customShimmer.stopShimmer()
        customShimmer.hideShimmer()
        customShimmer.visibility= View.GONE
    }
}
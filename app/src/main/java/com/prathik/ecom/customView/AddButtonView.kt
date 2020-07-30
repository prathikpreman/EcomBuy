package com.prathik.ecom.customView

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.prathik.ecom.R
import kotlinx.android.synthetic.main.addbutton_layout.view.*
import kotlinx.android.synthetic.main.customer_shimmer_layout.view.*

class AddButtonView:LinearLayout {

    lateinit var onCountUpdatedListener: OnCountChangedListener
    var isCancelDialogRequired=false

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
        LayoutInflater.from(context).inflate(R.layout.addbutton_layout, this, true)
        orientation = VERTICAL

        add_plus.setOnClickListener {
            setCount((add_count.text.toString()).toInt() + 1)
        }

        add_minus.setOnClickListener {

            if(isCancelDialogRequired && ((add_count.text.toString()).toInt() - 1)==0){

                showDialog()

            }else{
                setCount((add_count.text.toString()).toInt() - 1)
            }


        }

        add_addbtn.setOnClickListener {
            setCount(1)
        }
    }




   fun setCountListener(onCountUpdatedListener: OnCountChangedListener){
        this.onCountUpdatedListener=onCountUpdatedListener
    }

     fun setCount(count:Int){
         add_count.text=count.toString()
         if(count>0){
             add_addbtn.visibility=View.GONE
             add_countBtn.visibility=View.VISIBLE
         }else{
             add_addbtn.visibility=View.VISIBLE
             add_countBtn.visibility=View.GONE
         }
         onCountUpdatedListener.onCartCountUpdated(count)
     }


    fun setCountFromAdapter(count:Int?){
        add_count.text=count.toString()
        if(count!=null && count>0){
            add_addbtn.visibility=View.GONE
            add_countBtn.visibility=View.VISIBLE
        }else{
            add_addbtn.visibility=View.VISIBLE
            add_countBtn.visibility=View.GONE
        }
    }

    fun getCount():Int{
        return add_count.text as Int
    }


    interface OnCountChangedListener{
        fun onCartCountUpdated(count:Int)
    }


    fun showDialog(){

        AlertDialog.Builder(context).apply {

            setTitle("Remove from Cart ?")
            setMessage("Are you sure you  want to remove this item")
            setPositiveButton("Delete") { dialog, whichButton ->
                setCount(0)
            }
            setNegativeButton("Cancel") { dialog, whichButton ->
                dialog.dismiss()
            }
        }.create().show()
    }


}



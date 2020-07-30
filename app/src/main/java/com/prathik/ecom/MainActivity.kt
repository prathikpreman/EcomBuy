package com.prathik.ecom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Fade
import com.prathik.ecom.fragments.CartFragment
import com.prathik.ecom.fragments.HomeFragment
import com.prathik.ecom.fragments.OrderHistoryFragment
import com.prathik.ecom.utils.PreferenceManager
import com.roughike.bottombar.OnTabSelectListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {

    val TAG="HOMEACTIVITY67"

    lateinit var homeFragment:HomeFragment
    lateinit var orderHistoryFragment: OrderHistoryFragment
    lateinit var cartFragment: CartFragment

    private fun init(){
        toolbarBackBtn.visibility=View.GONE

        homeFragment= HomeFragment()
        orderHistoryFragment= OrderHistoryFragment()
        cartFragment= CartFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        init()

        Log.d(TAG,"USERID : ${PreferenceManager.getString(PreferenceManager.USER_ID,"")}")

        setBottonBar()






    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    private fun setBottonBar(){

        var fragmentManager = supportFragmentManager

        mainBottomBar.setOnTabSelectListener(OnTabSelectListener { tabId ->
            when(tabId){
                R.id.tab_home -> {

                    homeFragment.enterTransition=Fade(Gravity.LEFT)
                    homeFragment.exitTransition=Fade(Gravity.LEFT)
                    toolbarText.text="Hi ${PreferenceManager.getString(PreferenceManager.USER_NAME,"")}"

                    supportFragmentManager.beginTransaction().replace(R.id.home_container, homeFragment).commit()
                    mainBottomBar.visibility=View.VISIBLE
                    toolbarBackBtn.visibility=View.GONE
                }
                R.id.tab_history ->{

                    orderHistoryFragment.enterTransition=Fade(Gravity.LEFT)
                    orderHistoryFragment.exitTransition=Fade(Gravity.LEFT)

                    supportFragmentManager.beginTransaction().replace(R.id.home_container, orderHistoryFragment).commit()
                    mainBottomBar.visibility=View.VISIBLE
                    toolbarBackBtn.visibility=View.GONE
                }
                R.id.tab_noti ->{

//                    cartFragment.enterTransition=Fade(Gravity.LEFT)
//                    cartFragment.exitTransition=Fade(Gravity.LEFT)

                    supportFragmentManager.beginTransaction().replace(R.id.home_container, cartFragment).commit()
                    homeToolbarPad.visibility=View.VISIBLE

                    toolbarText.text="My Cart"


                    toolbarBackBtn.visibility=View.VISIBLE
                    toolbarBackBtn.setOnClickListener {

                        mainBottomBar.selectTabAtPosition(0)
                    }

                    mainBottomBar.visibility=View.GONE


                }
            }

        })
    }


    override fun onBackPressed() {

        if(mainBottomBar.currentTabPosition==0){
            super.onBackPressed()
        }else{
            mainBottomBar.selectTabAtPosition(0)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String) {
        if(event=="UP"){
            homeToolbarPad.visibility= View.VISIBLE
        }else{
            homeToolbarPad.visibility= View.GONE
        }
    }
}

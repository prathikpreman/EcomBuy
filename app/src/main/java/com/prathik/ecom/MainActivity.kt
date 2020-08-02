package com.prathik.ecom

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Fade
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.prathik.ecom.fragments.CartFragment
import com.prathik.ecom.fragments.HomeFragment
import com.prathik.ecom.fragments.OrderHistoryFragment
import com.prathik.ecom.realm.ProductRealmInstance
import com.prathik.ecom.utils.PreferenceManager
import com.roughike.bottombar.BottomBarTab
import com.roughike.bottombar.OnTabSelectListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {

    val TAG = "HOMEACTIVITY67"


    lateinit var homeFragment: HomeFragment
    lateinit var orderHistoryFragment: OrderHistoryFragment
    lateinit var cartFragment: CartFragment
    lateinit var cartTab: BottomBarTab

    private fun init() {

        toolbarBackBtn.visibility = View.GONE
        cartTab=mainBottomBar.getTabWithId(R.id.tab_cart)
        homeFragment = HomeFragment()
        orderHistoryFragment = OrderHistoryFragment()
        cartFragment = CartFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        init()

        Log.d(TAG, "USERID : ${PreferenceManager.getString(PreferenceManager.USER_ID, "")}")

        setBottonBar()

        checkForToken()

        setCartSize()


        toolbarSearch.setOnClickListener {
            startActivity(Intent(this,SearchActivity::class.java))
        }




    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }





    private fun setCartSize(){


        if(ProductRealmInstance.isCartItemAvaliable()){
            cartTab.setBadgeCount(ProductRealmInstance.getCartSize())
        }else{
            cartTab.removeBadge()
        }
    }

    private fun setBottonBar() {

        var fragmentManager = supportFragmentManager

        mainBottomBar.setOnTabSelectListener(OnTabSelectListener { tabId ->
            when (tabId) {
                R.id.tab_home -> {

                    homeFragment.enterTransition = Fade(Gravity.LEFT)
                    homeFragment.exitTransition = Fade(Gravity.LEFT)
                    toolbarText.text =
                        "Hi ${PreferenceManager.getString(PreferenceManager.USER_NAME, "")}"

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.home_container, homeFragment).commit()
                    mainBottomBar.visibility = View.VISIBLE
                    toolbarBackBtn.visibility = View.GONE
                }
                R.id.tab_history -> {

                    orderHistoryFragment.enterTransition = Fade(Gravity.LEFT)
                    orderHistoryFragment.exitTransition = Fade(Gravity.LEFT)

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.home_container, orderHistoryFragment).commit()
                    mainBottomBar.visibility = View.VISIBLE
                    toolbarBackBtn.visibility = View.GONE


                }
                R.id.tab_cart -> {

//                    cartFragment.enterTransition=Fade(Gravity.LEFT)
//                    cartFragment.exitTransition=Fade(Gravity.LEFT)

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.home_container, cartFragment).commit()
                    homeToolbarPad.visibility = View.VISIBLE

                    toolbarText.text = "My Cart"


                    toolbarBackBtn.visibility = View.VISIBLE
                    toolbarBackBtn.setOnClickListener {

                        mainBottomBar.selectTabAtPosition(0)
                    }

                    mainBottomBar.visibility = View.GONE


                }
            }

        })


    }


    private fun checkForToken() {
        if (PreferenceManager.getString(PreferenceManager.FCM_TOKEN, "NULL").equals("NULL")) {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    // 2
                    if (!task.isSuccessful) {
                        Log.w(TAG, "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }
                    // 3
                    val token = task.result?.token
                    Log.d(TAG, "FCM Token: $token")
                    PreferenceManager.setString(PreferenceManager.FCM_TOKEN, token)
                })
        }
    }


    override fun onBackPressed() {

        if (mainBottomBar.currentTabPosition == 0) {
            super.onBackPressed()
        } else {
            mainBottomBar.selectTabAtPosition(0)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String) {
        if (event == "UP") {
            homeToolbarPad.visibility = View.VISIBLE
        } else {
            homeToolbarPad.visibility = View.GONE
        }
    }
}


//cartPopup.visibility = View.GONE



//        if(ProductRealmInstance.isCartItemAvaliable()){
//            cartTab.setBadgeCount(ProductRealmInstance.getCartSize())
//        }else{
//            cartTab.removeBadge()
//        }

//        ProductRealmInstance.isCartItemAvaliable()
//        CartRealmObject.getCartSizeListener(object : CartRealmObject.OnCartSizeChangedListener {
//            override fun onCartSizeChanged(size: Int) {
//
//                Log.d(TAG, "CartSize: $size")
//
//                if (size > 0) {
//                    cartTab.setBadgeCount(size)
//                } else {
//                    cartTab.removeBadge()
//                }
//            }
//        })

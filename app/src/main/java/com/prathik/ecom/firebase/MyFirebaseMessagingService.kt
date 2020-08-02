package com.prathik.ecom.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.prathik.ecom.MainActivity
import com.prathik.ecom.R
import com.prathik.ecom.workmanager.RefreshAPIWorker.Companion.TAG
import java.util.logging.Handler

class MyFirebaseMessagingService: FirebaseMessagingService() {

    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM Message Recieved..."," ${remoteMessage.data} ")

        remoteMessage.notification?.let {
            Log.d( "FCM Message Recieved...","Title : ${it.title}")

            showNotification(it.title,it.body)
        }

        handleMessage(remoteMessage)




    }


    private fun handleMessage(remoteMessage: RemoteMessage) {



    }


    fun showNotification(title:String?, message:String?){

        val intent = Intent(this,MainActivity::class.java)

        //FLAG_UPDATE_CURRENT specifies that if a previous
        // PendingIntent already exists, then the current one
        // will update it with the
        // latest intent
        // 0 is the request code, using it later with the
        // same method again will get back the same pending
        // intent for future reference
        //intent passed here is to our afterNotification class
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = PendingIntent.getActivity(this, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        //RemoteViews are used to use the content of
        // some different layout apart from the current activity layout

      //  val contentView = RemoteViews(packageName, R.layout.activity_after_notification)

        //checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                channelId,description,NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this,channelId)
           //     .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
        }else{

            builder = Notification.Builder(this)
            //    .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234,builder.build())
    }



}

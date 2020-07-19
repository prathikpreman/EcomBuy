package com.prathik.ecom

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.prathik.ecom.utils.PreferenceManager
import java.util.*

class ApplicationClass:Application() {

    override fun onCreate() {
        super.onCreate()


        PreferenceManager.init(applicationContext)

        Places.initialize(applicationContext, getString(R.string.google_maps_key), Locale.US);
    }
}
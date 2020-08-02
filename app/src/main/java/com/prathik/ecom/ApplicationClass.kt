package com.prathik.ecom

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.prathik.ecom.utils.PreferenceManager
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*


class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()


        PreferenceManager.init(applicationContext)
        Realm.init(applicationContext)
        val config = RealmConfiguration
            .Builder()
            .name("default.realm")
            .schemaVersion(8)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)



        Places.initialize(applicationContext, getString(R.string.google_maps_key), Locale.US);
    }
}
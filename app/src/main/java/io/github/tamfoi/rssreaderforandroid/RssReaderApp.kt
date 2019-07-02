package io.github.tamfoi.rssreaderforandroid

import android.app.Application
import android.util.Log
import io.realm.Realm

class RssReaderApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("My", "init")
        //Realm.init(this)
    }
}
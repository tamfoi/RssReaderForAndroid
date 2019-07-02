package io.github.tamfoi.rssreaderforandroid

import android.app.Application
import io.realm.Realm

class RssReaderApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}
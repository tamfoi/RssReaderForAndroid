package io.github.tamfoi.rssreaderforandroid

import java.io.Serializable
import java.util.Date
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RssUrl : RealmObject(), Serializable {
    var url: String = ""

    // id をプライマリーキーとして設定
    @PrimaryKey
    var id: Int = 0
}
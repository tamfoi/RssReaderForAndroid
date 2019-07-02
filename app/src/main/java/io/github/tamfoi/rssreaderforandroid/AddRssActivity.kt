package io.github.tamfoi.rssreaderforandroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.ArrayAdapter
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_add_rss.*

class AddRssActivity : AppCompatActivity() {

    private lateinit var realmInstance: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_rss)

        realmInstance = Realm.getDefaultInstance()

        realmInstance.addChangeListener(object : RealmChangeListener<Realm> {
            override fun onChange(element: Realm) {
                reloadListView()
            }
        })

        addRssUrlButton.setOnClickListener {
            addRssUrl()
        }

        rssUrlListView.setOnItemLongClickListener { parent, view, position, id ->

            //RssUrlを削除する

            val deleteTargetUrl = parent.adapter.getItem(position).toString()

            // ダイアログを表示する
            val builder = AlertDialog.Builder(this)

            builder.setTitle("削除")
            builder.setMessage(deleteTargetUrl + "を削除しますか")

            builder.setPositiveButton("OK"){_, _ ->

                val results = realmInstance.where(RssUrl::class.java).equalTo("url", deleteTargetUrl).findAll()

                realmInstance.beginTransaction()
                results.deleteAllFromRealm()
                realmInstance.commitTransaction()

                reloadListView()
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }

        reloadListView()
    }

    private fun addRssUrl() {
        realmInstance.beginTransaction()

        val rssUrlRealmResults = realmInstance.where(RssUrl::class.java).findAll()

        val newRssUrl = RssUrl()

        newRssUrl.id =
            if (rssUrlRealmResults.max("id") != null) {
                rssUrlRealmResults.max("id")!!.toInt() + 1
            } else {
                0
            }

        newRssUrl.url = addRssUrlEditText.text.toString()

        realmInstance.copyToRealmOrUpdate(newRssUrl!!)
        realmInstance.commitTransaction()
    }

    private fun reloadListView() {
        val rssUrlRealmResults = realmInstance.where(RssUrl::class.java).findAll().sort("id", Sort.DESCENDING)

        val rssUrlRawList = realmInstance.copyFromRealm(rssUrlRealmResults)

        val rssUrlList = mutableListOf<String>()

        for (rssUrlRawItem in rssUrlRawList){
            rssUrlList.add(rssUrlRawItem.url)
        }

        rssUrlListView.adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, rssUrlList)
    }

    override fun onDestroy() {
        super.onDestroy()
        realmInstance.close()
    }
}

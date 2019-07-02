package io.github.tamfoi.rssreaderforandroid

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var asyncRssLoader : AsyncRssLoader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onResume(){
        super.onResume()

        createRssUrlSpinner()
        loadRss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item!!.itemId

        if(id == R.id.add_rss){
            val intent = Intent(applicationContext, AddRssActivity::class.java)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createRssUrlSpinner() {

        val realmInstance = Realm.getDefaultInstance()

        val rssUrlRealmResults = realmInstance.where(RssUrl::class.java).findAll().sort("id", Sort.DESCENDING)

        val rssUrlRawList = realmInstance.copyFromRealm(rssUrlRealmResults)

        val rssUrlList = mutableListOf<String>()

        for (rssUrlRawItem in rssUrlRawList){
            rssUrlList.add(rssUrlRawItem.url)
        }

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            rssUrlList
        )

        rssUrlSpinner.adapter = spinnerAdapter

        rssUrlSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadRss()
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        realmInstance.close()
    }

    private fun loadRss() {

        if(this.asyncRssLoader != null){
            this.asyncRssLoader!!.cancel(true)
        }

        if(rssUrlSpinner.selectedItem != null){
            this.asyncRssLoader = AsyncRssLoader()

            this.asyncRssLoader!!.url = rssUrlSpinner.selectedItem.toString()
            this.asyncRssLoader!!.onSuccess = {
                val adapter = SimpleAdapter(
                    this,
                    this.asyncRssLoader!!.result,
                    android.R.layout.simple_list_item_2,
                    arrayOf("title", "link"),
                    intArrayOf(android.R.id.text1, android.R.id.text2)
                )

                articleListView.adapter = adapter

                articleListView.setOnItemClickListener {parent, view, position, id ->
                    val uri = Uri.parse(this.asyncRssLoader!!.result[position]["link"])
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            }
            this.asyncRssLoader!!.execute()
        }

    }
}

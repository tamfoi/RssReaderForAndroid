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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var asyncRssLoader : AsyncRssLoader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val spinnerItem = listOf(
            "https://tamfoi.hatenablog.com/rss?aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "https://tamfoi.hatenablog.com/rss?bbb",
            "https://tamfoi.hatenablog.com/rss?cccccc"
        )

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            spinnerItem
        )

        rssUrlSppiner.adapter = spinnerAdapter

        rssUrlSppiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadRss()
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }



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

    fun loadRss() {

        if(this.asyncRssLoader != null){
            this.asyncRssLoader!!.cancel(true)
        }

        this.asyncRssLoader = AsyncRssLoader()

        this.asyncRssLoader!!.url = rssUrlSppiner.selectedItem.toString()
        this.asyncRssLoader!!.onSuccess = {
            val adapter = SimpleAdapter(
                this,
                this.asyncRssLoader!!.result,
                android.R.layout.simple_list_item_2,
                arrayOf("title", "link"),
                intArrayOf(android.R.id.text1, android.R.id.text2)
            )

            itemListView.adapter = adapter

            itemListView.setOnItemClickListener {parent, view, position, id ->
                val uri = Uri.parse(this.asyncRssLoader!!.result[position]["link"])
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
        this.asyncRssLoader!!.execute()

    }
}

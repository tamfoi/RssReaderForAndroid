package io.github.tamfoi.rssreaderforandroid

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SimpleAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var asyncRssLoader = AsyncRssLoader()
        asyncRssLoader.url = "https://tamfoi.hatenablog.com/rss"
        asyncRssLoader.onSuccess = {
            val adapter = SimpleAdapter(
                this,
                asyncRssLoader.result,
                android.R.layout.simple_list_item_2,
                arrayOf("title", "link"),
                intArrayOf(android.R.id.text1, android.R.id.text2)
            )

            itemListView.adapter = adapter

            itemListView.setOnItemClickListener {parent, view, position, id ->
                val uri = Uri.parse(asyncRssLoader.result[position]["link"])
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
        asyncRssLoader.execute()

    }
}

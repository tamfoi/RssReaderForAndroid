package io.github.tamfoi.rssreaderforandroid

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Xml
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AsyncLoadRss().execute()
    }

    inner class AsyncLoadRss: AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg p0: Void?): String? {
            val client = OkHttpClient()
            val request = Request.Builder().url("https://tamfoi.hatenablog.com/rss").get().build()
            val response = client.newCall(request).execute()
            return response.body()!!.string()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val xmlPullParser = Xml.newPullParser()
                xmlPullParser.setInput(StringReader(result))

                var eventType = xmlPullParser.eventType
                while (eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_DOCUMENT) {
                        Log.d("MyDebugger", "Start document")
                    } else if(eventType == XmlPullParser.START_TAG) {
                        Log.d("MyDebugger", "Start tag "+xmlPullParser.name)
                    } else if(eventType == XmlPullParser.END_TAG) {
                        Log.d("MyDebugger", "End tag "+xmlPullParser.name)
                    } else if(eventType == XmlPullParser.TEXT) {
                        Log.d("MyDebugger", "Text "+xmlPullParser.text)
                    }
                    eventType = xmlPullParser.next()
                }
            } catch (e: Exception) {}
        }
    }
}

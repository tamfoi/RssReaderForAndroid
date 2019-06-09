package io.github.tamfoi.rssreaderforandroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Xml
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        "https://tamfoi.hatenablog.com/rss"
            .httpGet()
            .responseString { request, response, result ->
                when (result){
                    is Result.Success -> {
                        Log.d("MyDebugger", result.get())
                        /*try {
                            val xmlPullParser = Xml.newPullParser()
                            xmlPullParser.setInput(StringReader(result.toString()))

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
                        } catch (e: Exception) {}*/
                    }
                    is Result.Failure -> {}
                }
            }
    }
}

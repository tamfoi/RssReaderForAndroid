package io.github.tamfoi.rssreaderforandroid

import android.os.AsyncTask
import android.util.Log
import android.util.Xml
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

class AsyncRssLoader: AsyncTask<Void, Void, String>() {

    var url: String? = null
    var onSuccess: (() -> Unit)? = null
    val result = mutableListOf<HashMap<String,String>>()

    override fun doInBackground(vararg p0: Void?): String? {
        if(this.url != null){
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(this.url).get().build()
                val response = client.newCall(request).execute()
                return response.body()!!.string()
            } catch (e: Exception) {
                return null
            }
        } else {
            return null
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if(result != null){
            try {
                val xmlPullParser = Xml.newPullParser()
                xmlPullParser.setInput(StringReader(result))

                var eventType = xmlPullParser.eventType

                var type = ""
                var title = ""
                var link = ""
                var isItemTag = false
                var isTitleTag = false
                var isLinkTag = false

                while (eventType != XmlPullParser.END_DOCUMENT){
                    when(eventType){
                        XmlPullParser.START_DOCUMENT -> {}
                        XmlPullParser.START_TAG -> {

                            when(xmlPullParser.name) {
                                //RSSのタイプを判定
                                "rss" -> {
                                    type = "rss"
                                }
                                "feed" -> {
                                    type = "atom"
                                }
                            }

                            //記事アイテムのタグに到達したらフラグを立てる
                            if((type == "rss" && xmlPullParser.name == "item") || (type == "atom" && xmlPullParser.name == "entry")){
                                isItemTag = true
                            }

                            if(isItemTag && xmlPullParser.name == "title") {
                                isTitleTag = true
                            }

                            if(isItemTag && xmlPullParser.name == "link") {
                                isLinkTag = true

                                if(type == "atom"){
                                    link = xmlPullParser.getAttributeValue("", "href")
                                }
                            }
                        }
                        XmlPullParser.END_TAG -> {

                            //記事アイテムのタグの終端まで到達したらフラグを下げる
                            if((type == "rss" && xmlPullParser.name == "item") || (type == "atom" && xmlPullParser.name == "entry")){
                                val item = HashMap<String,String>()
                                item.put("title", title)
                                item.put("link", link)
                                this.result.add(item)
                                isItemTag = false
                                title = ""
                                link = ""
                            }

                            if(isItemTag && xmlPullParser.name == "title") {
                                isTitleTag = false
                            }

                            if(isItemTag && xmlPullParser.name == "link") {
                                isLinkTag = false
                            }

                        }
                        XmlPullParser.TEXT -> {
                            if(isTitleTag){
                                title = xmlPullParser.text
                            }
                            if(isLinkTag && type == "rss"){
                                link = xmlPullParser.text
                            }
                        }
                    }
                    eventType = xmlPullParser.next()
                }

                if(onSuccess != null){
                    onSuccess?.invoke()
                }

            } catch (e: Exception) {}
        }
    }

}
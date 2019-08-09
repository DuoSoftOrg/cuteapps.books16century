package ru.cuteapps.books16century.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.squareup.picasso.Picasso
import ru.cuteapps.books16century.R
import org.json.JSONObject
import kotlinx.android.synthetic.main.items_news_app.view.*
import kotlinx.android.synthetic.main.items_news_app.view.image_iv
import kotlinx.android.synthetic.main.items_news_app.view.main_l
import kotlinx.android.synthetic.main.items_news_app.view.subtitle_tv
import kotlinx.android.synthetic.main.items_news_app.view.text_tv
import kotlinx.android.synthetic.main.items_news_app.view.title_tv
import kotlinx.android.synthetic.main.items_news_social.view.*
import ru.cuteapps.books16century.utils.ContentLoader
import ru.cuteapps.books16century.utils.Helper
import ru.cuteapps.books16century.utils.MessagingService
import ru.cuteapps.books16century.utils.loadUrl
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class NewsAdapter : ArrayAdapter<JSONObject> {

    private var items : ArrayList<JSONObject> = ArrayList()

    constructor (context : Context, loaded: () -> Unit ) : super (context, -1) {

        doAsync {
            try {
                val connection = URL(context.resources.getString(R.string.api_news)).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.doInput = true
                connection.doOutput = false
                connection.useCaches = false
                connection.connectTimeout = 10000
                connection.connect()

                Log.d("tag", connection.responseCode.toString())

                val reader = BufferedReader(InputStreamReader(BufferedInputStream(connection.inputStream)))
                val result = reader.use(BufferedReader::readText)

                connection.disconnect()

                val appsJson = JSONObject(result).getJSONArray("news")
                for(i in 0 until appsJson.length()) {
                    items.add(appsJson.getJSONObject(i))
                }
                uiThread {
                    it.clear()
                    it.addAll(items)
                    it.notifyDataSetChanged()
                    loaded()
                }
            } catch (ex : Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val appJson = items.get(position)
        var v : View? = null
        val type = appJson.getString("type")

        if (type == MessagingService.NOTIFICATION_TYPE_SOCIAL) {
            v = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.items_news_social, parent, false)
            v?.btn_action?.text = appJson.getString("btn_text")
            v?.btn_action?.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(appJson.getString("url"))
                context.startActivity(intent)
            }

        } else if (type == MessagingService.NOTIFICATION_TYPE_NEW_APP) {
            v = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.items_news_app, parent, false)

        } else if (type == MessagingService.NOTIFICATION_TYPE_UPDATE) {
            v = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.items_news_app, parent, false)

        }
        v?.title_tv?.text = appJson.getString("title")
        v?.text_tv?.text = appJson.getString("text")
        v?.subtitle_tv?.text = appJson.getString("date")
        Picasso.with(context).load(appJson.getString("poster")).into(v?.image_iv)
        v?.main_l?.setOnClickListener {
            try {
                if (appJson.has("callback_text")) {

                } else {

                }
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(appJson.getString("url"))
                context.startActivity(intent)
            } catch (e : Exception) {

            }
        }

        return v!!
    }

}
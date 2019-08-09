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
import kotlinx.android.synthetic.main.apps_item.view.*
import ru.cuteapps.books16century.utils.ContentLoader
import ru.cuteapps.books16century.utils.Helper
import ru.cuteapps.books16century.utils.loadUrl
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class AppsAdapter : ArrayAdapter<JSONObject> {

    private var items : ArrayList<JSONObject> = ArrayList()

    constructor (context : Context, loaded: () -> Unit ) : super (context, -1) {

        doAsync {
            try {
                val connection = URL(context.resources.getString(R.string.api_apps)).openConnection() as HttpURLConnection
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

                val appsJson = JSONObject(result).getJSONArray("apps")
                for(i in 0..(appsJson.length() - 1)) {
                    if (!appsJson.getJSONObject(i).getString("url").contains(context.packageName))
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
        val v = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.apps_item, parent, false)
        val appJson = items.get(position)

        v.title_tv.text = appJson.getString("title")
        Picasso.with(context).load(appJson.getString("poster")).into(v.image_iv)
        v.main_l.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(appJson.getString("url"))
                context.startActivity(intent)
            } catch (e : Exception) {

            }
        }

        if (context.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
            v.main_l.background = ColorDrawable(context.resources.getColor(R.color.colorDarkSecondary))
            v.card_view.background = ColorDrawable(context.resources.getColor(R.color.colorDarkPrimary))
            v.root_view.background = ColorDrawable(context.resources.getColor(R.color.colorDarkPrimary))
            v.title_tv.setTextColor(context.resources.getColor(R.color.colorDarkText))
        }

        return v
    }

}
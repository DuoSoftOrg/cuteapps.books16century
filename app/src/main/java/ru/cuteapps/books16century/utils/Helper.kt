package ru.cuteapps.books16century.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import com.squareup.picasso.Picasso
import ru.cuteapps.books16century.App
import ru.cuteapps.books16century.R
import org.json.JSONObject
import java.lang.Exception

object Helper {
    var newLine = System.getProperty("line.separator")

    var PREFERENCES_NAME = "prefs"
    var PREF_LAUNCH_COUNTER = "launch_counter"
    var PREF_VOTED = "voted"

    var isFirstLaunch = false

    fun isRussianLocale (context: Context) : Boolean {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0).language
        } else {
            context.resources.configuration.locale.language
        }
        return locale == "ru" || locale == "uk" || locale == "be" || locale == "kk"
    }

    fun upFirstLetter(word: String?): String {
        return if (word != null)
            word.substring(0, 1).toUpperCase() + word.substring(1)
        else
            ""
    }
    fun escapeSqlParam(sqlParam: String): String {
        return sqlParam.replace("'".toRegex(), "''")
    }

    fun goToTelegram (context : Context) {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("tg://resolve?domain=duosoft_books")
            context.startActivity(i)
        } catch (e : Exception) {
            e.printStackTrace()
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://t.me/duosoft_books")
            context.startActivity(i)
        }
    }

    fun goToVk (context: Context) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("https://vk.com/duosoft_books")
        context.startActivity(i)
    }

    fun goToYt(context: Context) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("https://www.youtube.com/channel/UCyolXyg1moleWU287On_AaQ?sub_confirmation=1")
        context.startActivity(i)
    }

    fun goToInsta(context: Context) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("https://www.instagram.com/vestnik.knig/")
        context.startActivity(i)
    }

    fun getSocialsFile () : JSONObject? {
        var socials : JSONObject? = null
        try {

        } catch (e : Exception) {

        }
        return socials
    }

    fun vkExists() : Boolean {
        var exists = false
        try {
            App.getContext().packageManager.getPackageInfo("com.vkontakte.android", 0)
            exists = true
        } catch (e: java.lang.Exception) {
            exists = false
        }
        if (!exists) {
            try {
                App.getContext().packageManager.getPackageInfo("com.perm.kate_new_6", 0)
                exists = true
            } catch (e: java.lang.Exception) {
                exists = false
            }
        }
        return exists
    }

    fun tgExists() : Boolean {
        var exists = false
        try {
            App.getContext().packageManager.getPackageInfo("org.telegram.messenger", 0)
            exists = true
        } catch (e: java.lang.Exception) {
            exists = false
        }
        if (!exists) {
            try {
                App.getContext().packageManager.getPackageInfo("org.thunderdog.challegram", 0)
                exists = true
            } catch (e: java.lang.Exception) {
                exists = false
            }
        }
        return exists
    }


}
fun ImageView.loadUrl (url: String, context: Context) {
    if (Helper.isRussianLocale(context)) {
        Picasso.with(context).load(url)
                .error(resources.getDrawable(R.drawable.book_app_noposter))
                .placeholder(resources.getDrawable(R.drawable.book_app_noposter))
                .into(this)
    } else {
        Picasso.with(context).load(R.drawable.book_app_noposter)
                .error(resources.getDrawable(R.drawable.book_app_noposter))
                .placeholder(resources.getDrawable(R.drawable.book_app_noposter))
                .into(this)
    }
}


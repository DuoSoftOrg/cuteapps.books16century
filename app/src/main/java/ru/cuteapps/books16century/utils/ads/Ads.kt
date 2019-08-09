package ru.cuteapps.books16century.utils.ads

import android.content.Context
import android.util.Log
import ru.cuteapps.books16century.App
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


object Ads {

    val API_FOLDER = "http://duosoft.org/api/ads/"

    var initialized = false
    var locale = App.getContext().resources.configuration.locale.language;
    var errorMessage = ""

    fun build (success: (banners: ArrayList<Banner>, pagers: ArrayList<Pager>) -> Unit, error: (message: String) -> Unit) {

        doAsync {

            try {
                val connection = URL("$API_FOLDER/ads.json").openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.doInput = true
                connection.doOutput = true
                connection.connect()

                val reader = BufferedReader(InputStreamReader(BufferedInputStream(connection.inputStream)))
                val result = reader.use(BufferedReader::readText)

                val bannersObj = JSONObject(result).getJSONArray("banners")
                val bannersList = ArrayList<Banner>()
                for (i in 0 until bannersObj.length()) {
                    val bannerObj = bannersObj.getJSONObject(i)
                    val banner = Banner()
                    banner.name = bannerObj.getString("name")
                    banner.bannerUrl = bannerObj.getString("bannerUrl")
                    banner.partnerUrl = bannerObj.getString("partnerUrl")
                    banner.locale = bannerObj.getString("locale")
                    banner.enabled = bannerObj.getBoolean("enabled")
                    banner.viewCounter = App.getContext().getSharedPreferences("ad_prefs", Context.MODE_PRIVATE).getInt("ads_banner_viewCounter_${banner.name}", 0)
                    banner.clicked = App.getContext().getSharedPreferences("ad_prefs", Context.MODE_PRIVATE).getBoolean("ads_banner_clicked_${banner.name}", false)
                    bannersList.add(banner)
                }

                val pagersObj = JSONObject(result).getJSONArray("pagers")
                val pagersList = ArrayList<Pager>()
                for (i in 0 until pagersObj.length()) {
                    val pagerObj = pagersObj.getJSONObject(i)
                    val pager = Pager()
                    pager.name = pagerObj.getString("name")
                    pager.bannerUrl = pagerObj.getString("bannerUrl")
                    pager.partnerUrl = pagerObj.getString("partnerUrl")
                    pager.locale = pagerObj.getString("locale")
                    pager.enabled = pagerObj.getBoolean("enabled")
                    pager.viewCounter = App.getContext().getSharedPreferences("ad_prefs", Context.MODE_PRIVATE).getInt("ads_pager_viewCounter_${pager.name}", 0)
                    pager.clicked = App.getContext().getSharedPreferences("ad_prefs", Context.MODE_PRIVATE).getBoolean("ads_pager_clicked_${pager.name}", false)
                    pagersList.add(pager)
                }

                initialized = true
                if (!locale.equals("en"))
                    locale = "ru"

                uiThread {
                    success(bannersList, pagersList)
                }

            } catch (ex: Exception) {
                initialized = false
                errorMessage = "Не удалось загрузить рекламу"
                ex.printStackTrace()
                uiThread {
                    error(errorMessage)
                }
            }
        }

    }

    fun getPersonalAltBanner(banners: ArrayList<Banner>): Banner {
        var banner: Banner? = null
        for (i in 0 until banners.size) {
            if (!banners[i].enabled)
                continue
            if (banners[i].clicked)
                continue
            if (!banners[i].locale.equals(locale))
                continue
            Log.d("tag", banners[i].clicked.toString())
            banner = banners[i]
            break
        }
        if (banner == null) {
            for (i in 0 until banners.size) {
                if (!banners[i].enabled)
                    continue
                banner = banners[i]
                break
            }
        }
        return banner!!
    }

    fun getPersonalAltPager(pagers: ArrayList<Pager>): Pager {
        var pager: Pager? = null
        for (i in 0 until pagers.size) {
            if (!pagers[i].enabled)
                continue
            if (pagers[i].clicked)
                continue
            if (!pagers[i].locale.equals(locale))
                continue
            pager = pagers[i]
            break
        }
        if (pager == null) {
            for (i in 0 until pagers.size) {
                if (!pagers[i].enabled)
                    continue
                pager = pagers[i]
                break
            }
        }
        return pager!!
    }


}
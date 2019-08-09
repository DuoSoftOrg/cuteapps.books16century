package ru.cuteapps.books16century.utils
/*
import android.app.Activity
import android.content.Context
import android.util.Log
import ru.apperate.ads.InterstitialAd
import java.util.*
import android.net.NetworkInfo
import android.net.ConnectivityManager
import ru.cuteapps.books16century.shop.Shop


/* Created by admin on 14.08.2018. */
object ApperateAd {

    var adIsShowing = false

    private var interstitialAd: InterstitialAd? = null

    fun closeApperateAd () {
        if (interstitialAd != null)
            interstitialAd!!.onEventClosed()
    }

    fun showApperateAd (activity: Activity) {

        return

        if (Shop.isAdFree)
            return

        if (!isNetworkAvailable(activity))
            return

        val lastAd = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).getLong("apperate_lastadshow", 0)
        val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val lastAdDay = Calendar.getInstance()
        lastAdDay.timeInMillis = lastAd
        val lastAdShowDay = lastAdDay.get(Calendar.DAY_OF_YEAR)

        if (lastAdShowDay != today) {

            interstitialAd = ru.apperate.ads.InterstitialAd(activity)
            interstitialAd!!.setInterstitialAdListener(object : ru.apperate.ads.delegate.InterstitialAdListener {
                override fun onInterstitialLoaded(p0: InterstitialAd?) {
                    Log.d("tag", "реклама загружена")
                    if (interstitialAd != null)
                        interstitialAd!!.show()
                    activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putLong("apperate_lastadshow", Date().time).apply()
                }

                override fun onInterstitialClicked() {
                    adIsShowing = false
                    activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putLong("apperate_lastadshow", Date().time).apply()
                }

                override fun onInterstitialError(p0: ru.apperate.ads.Error?) {
                    Log.d("tag", "ошибка загрузки: " + p0!!.message)
                    adIsShowing = true
                    activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putLong("apperate_lastadshow", Date().time).apply()
                }

                override fun onInterstitialClosed() {
                    Log.d("tag", "реклама закрыта")
                    adIsShowing = false
                    activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putLong("apperate_lastadshow", Date().time).apply()
                }

                override fun onInterstitialShowed() {
                    Log.d("tag", "реклама показана")
                    adIsShowing = true
                    activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putLong("apperate_lastadshow", Date().time).apply()
                }

            })
            interstitialAd!!.load()

        }
    }

    private fun isNetworkAvailable(activity: Activity): Boolean {
        try {
            val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }catch (e : Exception) {
            return false
        }
    }

}
*/
package ru.cuteapps.books16century.utils

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.yandex.mobile.ads.AdRequest
import ru.cuteapps.books16century.App
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.R.*
import java.lang.Exception

/* Created by admin on 22.11.2018. */
object Ad {

    fun init (context: Context) {
        try {
            MobileAds.initialize(App.getContext(), context.resources.getString(string.ads_app_id))
            loadGooglePager(context)
        } catch (e : Exception) {

        }

    }

    var googlePager : InterstitialAd? = null
    var googlePagerIsLoaded = false

    fun showGooglePager () {
        if (googlePager != null && googlePager!!.isLoaded)
            googlePager?.show()
    }

    fun loadGooglePager(context: Context) {
        googlePagerIsLoaded = false
        if (googlePager == null) {
            googlePager = InterstitialAd(context)
            //googlePager?.adUnitId = "ca-app-pub-3940256099942544/1033173712"
            googlePager?.adUnitId = context.resources.getString(string.unit_id_pager)
            googlePager?.adListener = object: AdListener() {
                override fun onAdLoaded() {
                    googlePagerIsLoaded = true
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    googlePagerIsLoaded = false
                }

                override fun onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                override fun onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                override fun onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                override fun onAdClosed() {
                    googlePagerIsLoaded = false
                    googlePager?.loadAd(com.google.android.gms.ads.AdRequest.Builder().build())
                }
            }
        }
        googlePager?.loadAd(com.google.android.gms.ads.AdRequest.Builder().build())
    }


}
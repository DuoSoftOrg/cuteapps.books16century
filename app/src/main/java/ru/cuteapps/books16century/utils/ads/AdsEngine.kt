package ru.cuteapps.books16century.utils.ads

import android.util.Log

object AdsEngine {

    private var altAdsInitialized = false
    private var altAdsInitCounter = 0
    private var banners : ArrayList <Banner> = ArrayList()
    private var pagers : ArrayList <Pager> = ArrayList()

    private fun initAlternativeAds (ready: () -> Unit) {
        Ads.build(success = { banners, pagers ->
            this.banners = banners
            this.pagers = pagers
            altAdsInitialized = true
            ready()
        }, error = {message ->
            Log.d("tag", message)
            altAdsInitialized = false
            altAdsInitCounter++
        })
    }

    fun getAltBanner (success: (banner: Banner) -> Unit){
        if (altAdsInitialized && banners.size > 0) {
            val banner = Ads.getPersonalAltBanner(banners)
            success(banner)
        } else if (altAdsInitCounter < 3) {
            initAlternativeAds (ready = {
                val banner = Ads.getPersonalAltBanner(banners)
                success(banner)
            })
        }
    }

    fun getAltPager (success: (banner: Pager) -> Unit) {
        if (altAdsInitialized && pagers.size > 0) {
            val pager = Ads.getPersonalAltPager(pagers)
            success(pager)
        } else if (altAdsInitCounter < 3) {
            initAlternativeAds (ready = {
                val pager = Ads.getPersonalAltPager(pagers)
                success(pager)
            })
        }
    }

}
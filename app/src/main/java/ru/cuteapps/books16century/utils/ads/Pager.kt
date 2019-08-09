package ru.cuteapps.books16century.utils.ads

import android.content.Context
import ru.cuteapps.books16century.App

class Pager {
    var name = ""
    var bannerUrl = ""
    var partnerUrl = ""
    var locale = "en"
    var clicked = false
    var enabled = true
    var viewCounter = 0

    fun setClicked() {
        clicked = true
        App.getContext().getSharedPreferences("ad_prefs", Context.MODE_PRIVATE).edit().putBoolean("ads_pager_clicked_${this.name}", true).apply()
    }
}
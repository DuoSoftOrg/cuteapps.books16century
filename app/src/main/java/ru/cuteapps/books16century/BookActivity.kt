package ru.cuteapps.books16century

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds

import kotlinx.android.synthetic.main.activity_item.*
import ru.cuteapps.books16century.model.Book
import ru.cuteapps.books16century.shop.Shop
import ru.cuteapps.books16century.utils.*
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.*
import androidx.viewpager.widget.ViewPager
import com.tooltip.Tooltip
import com.yandex.mobile.ads.AdEventListener
import com.yandex.mobile.ads.AdRequestError
import com.yandex.mobile.ads.InterstitialEventListener
import org.jetbrains.anko.*
import ru.cuteapps.books16century.adapter.PagerAdapter
import ru.cuteapps.books16century.dialogs.SocialPicker
import ru.cuteapps.books16century.dialogs.VoteDialog
import ru.cuteapps.books16century.model.Bookmark
import ru.cuteapps.books16century.utils.Requests
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

class BookActivity : AppCompatActivity() {

    private var menu: Menu? = null

    companion object {
        var isRunning = false
        var isFocusedOnScreen = false
        const val RESULT_GO_TO_BOOKMARK = 1
        const val RESULT_SETTINGS_CHANGES = 2
        const val RESULT_BOOK_IS_NULL = 0
        lateinit var currentBook : Book
        var currentBookmark : Bookmark? = null
    }

    override fun onStart() {
        isRunning = true
        isFocusedOnScreen = true
        //Log.d("tag", "onStart")
        super.onStart()
    }

    override fun onResume() {
        isFocusedOnScreen = true
        //Log.d("tag", "onResume")
        super.onResume()
    }

    override fun onStop() {
        isFocusedOnScreen = false
        //Log.d("tag", "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        isRunning = false
        isFocusedOnScreen = false
        //Log.d("tag", "onDestroy")
        super.onDestroy()
    }

    override fun onPause() {
        isFocusedOnScreen = false
        //Log.d("tag", "onPause")
        super.onPause()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.d("tag", "onCreate")

        setContentView(R.layout.activity_item)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val book = Db.getItem(intent.getIntExtra("_id", 0))
        if (book == null) {
            setResult(RESULT_BOOK_IS_NULL)
            finish()
            return
        }
        currentBook = book

        if (intent.getIntExtra("bookmark_id", -1) != -1) {
            currentBookmark = Db.getBookmark(intent.getIntExtra("bookmark_id", -1))
            currentBook.page = currentBookmark?.page ?: 0
        }

        Db.addViewedBook(currentBook!!.id)

        //this.title = book.title + "\n" + "блабла"
        title_tv.text = currentBook.title
        subtitle_tv.text = "часть " + (currentBook.page + 1).toString() + " из " + currentBook.pages

        loadBook()

        if (!VoteDialog.showInstance(this)) {
            setupAds()
        }

    }

    private fun showNewsActivity () {
        val int = Intent(this, NewsActivity::class.java)
        startActivity(int)
    }

    private fun setupAds() {

        yAdView.visibility = View.GONE
        gAdView.visibility = View.GONE

        if (Shop.isAdFree) {
            Log.d("tag", "я адфри")
            yAdView.visibility = View.GONE
            gAdView.visibility = View.GONE

            try {
                val menuItem = menu?.findItem(R.id.action_ads)
                menuItem!!.isVisible = false
                menuItem.isEnabled = false
            } catch (e: Exception) {

            }

        } else {

            try {

                gAdView.visibility = View.VISIBLE

                gAdView.adListener = object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {


                        if (yAdView.blockId.isNullOrBlank())
                            yAdView.blockId = getString(R.string.yandex_banner)
                        if (yAdView.adSize == null)
                            yAdView.adSize = com.yandex.mobile.ads.AdSize.BANNER_320x100

                        yAdView.visibility = View.VISIBLE
                        yAdView.adEventListener = object : AdEventListener {
                            override fun onAdLoaded() {
                                gAdView.visibility = View.GONE
                                yAdView.visibility = View.VISIBLE
                            }

                            override fun onAdOpened() {}
                            override fun onAdLeftApplication() {}
                            override fun onAdFailedToLoad(error: AdRequestError) {

                            }

                            override fun onAdClosed() {}
                        }

                        yAdView.loadAd(com.yandex.mobile.ads.AdRequest.Builder().build())


                    }
                }
                gAdView.loadAd(AdRequest.Builder().build())
            } catch (e: Exception) {

            }

            if (currentBookmark == null && !intent.getBooleanExtra("fast_load", false)) {

                try {
                    if (Ad.googlePagerIsLoaded) {
                        Ad.showGooglePager()
                    } else {
                        Ad.loadGooglePager(this)

                        val yPager = com.yandex.mobile.ads.InterstitialAd(this@BookActivity)
                        yPager.blockId = getString(R.string.yandex_pager)
                        yPager.interstitialEventListener = object : InterstitialEventListener {

                            override fun onInterstitialLoaded() {
                                if (yPager.isLoaded) {
                                    if (isFocusedOnScreen)
                                        yPager.show()
                                }
                            }

                            override fun onInterstitialShown() {

                            }

                            override fun onAdOpened() {

                            }

                            override fun onInterstitialDismissed() {}
                            override fun onInterstitialFailedToLoad(error: AdRequestError?) {

                            }

                            override fun onAdClosed() {}
                            override fun onAdLeftApplication() {}

                        }
                        yPager.loadAd(com.yandex.mobile.ads.AdRequest.Builder().build())
                    }
                } catch (e: Exception) {

                }

            }
        }

    }

    private var canShowInterstitial = true
    private fun showInterstitialAds() {
        Log.d("tag", canShowInterstitial.toString())
        if (!canShowInterstitial)
            return

        canShowInterstitial = false

        Timer("background_keeper", false).schedule(2 * 60 * 1000) {
            canShowInterstitial = true
        }

        val pagerInterstitial = InterstitialAd(App.getContext())
        pagerInterstitial.adUnitId = App.getContext().getString(R.string.unit_id_pager_interstitial)
        pagerInterstitial.adListener = object : AdListener() {
            override fun onAdLoaded() {
                if (pagerInterstitial.isLoaded) {
                    if (isRunning)
                        pagerInterstitial.show()
                }
            }

            override fun onAdClosed() {}
            override fun onAdFailedToLoad(errorCode: Int) {
                val yPagerIterstitial = com.yandex.mobile.ads.InterstitialAd(this@BookActivity)
                yPagerIterstitial.blockId = getString(R.string.yandex_pager)
                yPagerIterstitial.interstitialEventListener = object : InterstitialEventListener {

                    override fun onInterstitialLoaded() {
                        if (yPagerIterstitial.isLoaded) {
                            if (isRunning)
                                yPagerIterstitial.show()
                        }
                    }

                    override fun onInterstitialShown() {}
                    override fun onAdOpened() {}
                    override fun onInterstitialDismissed() {}
                    override fun onInterstitialFailedToLoad(error: AdRequestError?) {
                    }

                    override fun onAdClosed() {}
                    override fun onAdLeftApplication() {}

                }
                yPagerIterstitial.loadAd(com.yandex.mobile.ads.AdRequest.Builder().build())

            }
        }
        pagerInterstitial.loadAd(AdRequest.Builder().build())

    }

    private fun loadBook() {
        showLoading()

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                //Log.d("tag", pagerAdapter!!.getRegisteredFragment(viewPager.currentItem).page.toString() + " страница")
                currentBook.page = position
                ContentLoader.setPageIndex(currentBook.id, currentBook.page)
                if (!Shop.isAdFree && (currentBook.page) >= 3 && (currentBook.page) % 2 != 0) {
                    showInterstitialAds()
                }

                subtitle_tv.text = "часть " + (currentBook.page + 1).toString() + " из " + currentBook.pages
            }
        })

        if (ContentLoader.bookExists(currentBook.id, currentBook.page)) {
            val pagerAdapter = PagerAdapter(supportFragmentManager)
            viewPager.adapter = pagerAdapter
            viewPager.currentItem = currentBook.page
            hideLoading()
        } else {
            ContentLoader.downloadBook(currentBook.id, {
                val pagerAdapter = PagerAdapter(supportFragmentManager)
                viewPager.adapter = pagerAdapter
                viewPager.currentItem = currentBook.page
                hideLoading()
            }, { errorMessage ->
                longToast(errorMessage)
                hideLoading()
            })
        }

    }

    private fun showSocialPromo() {

        if (currentBookmark != null) {
            return
        }

        doAsync {
            var message = ""
            val cancelable: Boolean
            val tgExists = Helper.tgExists()
            val vkExists = Helper.vkExists()

            val firstLaunch = getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("first_book_launch", true)

            if (firstLaunch) {
                //в первый раз предложим только Телегу, если есть
                getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("first_book_launch", false).apply()
                if (tgExists) {
                    message = resources.getString(R.string.tg_promo_first)
                }
                else if (vkExists) {
                    message = resources.getString(R.string.vk_promo_first)
                } else {
                    message = resources.getString(R.string.social_promo)
                }

            } else {
                message = resources.getString(R.string.social_promo)

                var launchCounter = getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("tg_promo_counter", 0)
                if (launchCounter > 3) {
                    getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putInt("tg_promo_counter", 0).apply()
                } else {
                    launchCounter++
                    getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putInt("tg_promo_counter", launchCounter).apply()
                    return@doAsync
                }
            }

            Thread.sleep(500)

            uiThread {
                try {
                    var bindView = findViewById<View>(R.id.action_socials)
                    if (bindView == null)
                        bindView = findViewById<View>(R.id.action_tg)
                    if (bindView == null)
                        bindView = findViewById<View>(R.id.action_vk)

                    Tooltip.Builder(bindView, R.style.TooltipTG)
                            .setDismissOnClick(true)
                            .setCancelable(false)
                            .setGravity(Gravity.BOTTOM)
                            .setText(message)
                            .setOnClickListener {
                                //goToTelegram(this@BookActivity)
                            }
                            .setOnDismissListener {

                            }
                            .show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    }

    private fun showLoading() {
        progress_l.visibility = View.VISIBLE
        viewPager.visibility = View.GONE
    }

    private fun hideLoading() {
        progress_l.visibility = View.GONE
        viewPager.visibility = View.VISIBLE
    }

    private fun setDarkTheme() {
        toolbar.setTitleTextColor(Color.parseColor("#f0f2f3"))
        toolbar.setBackgroundColor(Color.parseColor("#202425"))
        root_view.setBackgroundColor(Color.parseColor("#202425"))
        appbar_l.setBackgroundColor(Color.parseColor("#202425"))
        main_frame.setBackgroundColor(Color.parseColor("#202425"))
        window.statusBarColor = Color.parseColor("#202425")
        window.navigationBarColor = Color.parseColor("#202425")
        /*try {
            val icon = toolbar.menu.findItem(R.id.nav_bookmarks).icon
            icon.mutate()
            icon.setColorFilter(resources.getColor(android.R.color.white), PorterDuff.Mode.SRC_IN)
        } catch (e : Exception) {

        }*/
    }

    private fun setLightTheme() {
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"))

        toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        root_view.setBackgroundColor(resources.getColor(android.R.color.white))
        appbar_l.setBackgroundColor(resources.getColor(android.R.color.white))
        main_frame.setBackgroundColor(resources.getColor(android.R.color.white))
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        window.navigationBarColor = resources.getColor(R.color.colorPrimary)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)

        val item = menu.findItem(R.id.action_fav)
        this.menu = menu

        if (Db.isFav(currentBook.id)) {
            item.icon = getDrawable(R.drawable.ic_fav_full)
            item.title = getString(R.string.action_title_remove_from_fav)
        } else {
            item.icon = getDrawable(R.drawable.ic_fav_empty)
            item.title = getString(R.string.action_title_add_to_fav)
        }

        val vkItem = menu.findItem(R.id.action_vk)
        val tgItem = menu.findItem(R.id.action_tg)
        val socialItem = menu.findItem(R.id.action_socials)
        tgItem.isVisible = false
        vkItem.isVisible = false
        socialItem.isVisible = false

        val ifTg = Helper.tgExists()
        val ifVk = Helper.vkExists()

        if (Helper.isRussianLocale(this)) {
            if (Helper.isFirstLaunch) {
                if (ifTg) {
                    tgItem.isVisible = true
                } else if (ifVk) {
                    vkItem.isVisible = true
                }
            } else {
                socialItem.isVisible = true
            }
        }


        if (isFocusedOnScreen) {
            showSocialPromo()
        }

        if (Shop.isAdFree) {
            menu.findItem(R.id.action_ads)?.isVisible = false
        }

        if (getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
            setDarkTheme()
            menu.findItem(R.id.action_enable_dark_theme).isChecked = true
        }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            android.R.id.home -> onBackPressed()

            R.id.action_help -> {
                alert("Для перемещения к следующей или предыдущей части книги используйте свайп влево и вправо. Для прокрутки текста страницы используйте свайп вверх и вниз. \nСохранение прогресса чтения происходит автоматически. Настроить размер шрифта, цветовую схему, сохранить закладку можно в меню. \nСледить за новостями и обновлениями, задать вопрос и внести предложения можно в соцсетях.", "Управление и навигация") {
                    yesButton {  }
                }.show()
            }

            R.id.action_fav -> {

                if (Db.isFav(currentBook.id)) {
                    Db.setFav(currentBook.id, false)
                    item.icon = getDrawable(R.drawable.ic_fav_empty)
                    item.title = getString(R.string.action_title_add_to_fav)
                    longToast(R.string.text_fav_remove)
                } else {
                    Db.setFav(currentBook.id, true)
                    item.icon = getDrawable(R.drawable.ic_fav_full)
                    item.title = getString(R.string.action_title_remove_from_fav)
                    longToast(R.string.text_fav_add)
                }
            }

            R.id.action_bookmarks -> {
                val intent = Intent(this, BookmarksActivity::class.java)
                intent.putExtra("id", currentBook.id)
                startActivityForResult(intent, Requests.BOOKMARKS_ACTIVITY_REQUEST)
            }

            R.id.action_bookmark -> {
                try {
                    (viewPager.adapter as PagerAdapter).getRegisteredFragment(viewPager.currentItem).addBookmark()
                    //(viewPager.adapter as PagerAdapter).addBookmark(viewPager.currentItem)
                } catch (e: Exception) {

                }

            }

            R.id.action_vk -> {
                Helper.goToVk(this)
            }

            R.id.action_tg -> {
                Helper.goToTelegram(this)
            }

            R.id.action_socials -> {
                val dialog = SocialPicker ()
                dialog.show(supportFragmentManager, "social")
            }

            R.id.action_plus_text -> {
                try {
                    (viewPager.adapter as PagerAdapter).plusText()
                } catch (e: Exception) {

                }
            }

            R.id.action_minus_text -> {
                try {
                    (viewPager.adapter as PagerAdapter).minusText()
                } catch (e: Exception) {

                }
            }

            R.id.action_ads -> {
                try {
                    Shop.buyAdFree(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            R.id.action_enable_dark_theme -> {
                try {
                    if (item.isChecked) {
                        setLightTheme()
                        (viewPager.adapter as PagerAdapter).setLightTheme()
                        item.isChecked = false
                        getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putBoolean("theme_dark", false).apply()
                    } else {
                        setDarkTheme()
                        (viewPager.adapter as PagerAdapter).setDarkTheme()
                        item.isChecked = true
                        getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putBoolean("theme_dark", true).apply()
                    }
                    setResult(RESULT_SETTINGS_CHANGES)
                } catch (e : Exception) {

                }
            }

            R.id.action_news -> {
                showNewsActivity()
            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Requests.BOOKMARKS_ACTIVITY_REQUEST && resultCode == BookmarksActivity.RESULT_BOOKMARK_SELECTED) {
            setResult(RESULT_GO_TO_BOOKMARK, data)
            finish()
        }
    }

    override fun onBackPressed() {
        if (intent.getBooleanExtra("fast_load", false)) {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        super.onBackPressed()
    }

}

package ru.cuteapps.books16century.widget

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_page.view.*
import ru.cuteapps.books16century.BookActivity
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.model.Bookmark
import ru.cuteapps.books16century.model.Book
import ru.cuteapps.books16century.utils.ContentLoader
import ru.cuteapps.books16century.utils.Db
import ru.cuteapps.books16century.utils.Helper
import org.jetbrains.anko.longToast
import java.util.*
import kotlin.concurrent.schedule

/* Created by admin on 17.04.2019. */
class PageFragment : Fragment() {

    var page = 0

    var mView : View? = null

    companion object {
        fun newInstance(pageIndex : Int): PageFragment {
            val fragment = PageFragment()

            val bundle = Bundle(1)
            bundle.putInt("page", pageIndex)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("page", page)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = arguments!!.getInt("page", 0)
        if (savedInstanceState != null)
            page = savedInstanceState.getInt("page", page)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var v : View? = null
        try {
            v = inflater.inflate(R.layout.fragment_page, container, false)
        } catch (e : java.lang.Exception) {

        }
        if (v == null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    WebView.setDataDirectorySuffix ("suffix-" + page.toString())
                }
                v = inflater.inflate(R.layout.fragment_page, container, false)
            } catch (e : java.lang.Exception) {
                return View(context)
            }
        }

        try {

            v!!.webView.loadData(ContentLoader.getPageContentByIndex(BookActivity.currentBook.id, page), "text/html; charset=utf-8", "utf-8")

            val setupProgressBar = {
                val realContentHeight = (v.webView.contentHeight * v.webView.scale).toInt()
                v.progressBar.visibility = View.VISIBLE
                v.progressBar.clearAnimation()
                v.progressBar.max = realContentHeight - v.webView.height
                v.progressBar.progress = v.webView.scrollY + 1
            }

            v.webView.setOnSizeChangeListener {
                setupProgressBar()
            }
            v.webView.webViewClient = object : WebViewClient() {

                override fun onPageFinished(mView: WebView?, url: String?) {
                    super.onPageFinished(mView, url)

                    val zoom = mView!!.context.getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("webview_zoom", 0)
                    if (zoom != 0) {
                        mView.webView.settings.textZoom = zoom
                    }

                    if (BookActivity.currentBookmark != null && BookActivity.currentBookmark!!.page == page) {
                        mView.webView.scrollY = BookActivity.currentBookmark?.progress ?: 0
                        ContentLoader.setPageIndex(BookActivity.currentBook.id, page)
                        saveWebViewPosition()
                    } else {
                        mView.webView.scrollY = getWebViewPosition()
                    }

                    if (mView.context.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
                        setDarkTheme()
                    }

                    setupProgressBar()
                }
            }

            val listener = ObservableWebView.OnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->

                val realContentHeight = (v.webView.contentHeight * v.webView.scale).toInt()

                if (v.progressBar.max == 0 || realContentHeight != v.progressBar.max) {
                    setupProgressBar()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.progressBar.setProgress(scrollY, true)
                } else {
                    v.progressBar.setProgress(scrollY)
                }

            }

            v.webView.onScrollChangeListener = listener

            //прогресс бар

            setupProgressBar()

        } catch (e : java.lang.Exception) {

        }

        mView = v
        return mView
    }

    private fun saveWebViewPosition() {
        try {
            mView!!.context.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putInt("webview_position_item=${BookActivity.currentBook.id}_page=${page}", mView!!.webView.scrollY).apply()
        } catch (e : java.lang.Exception) {

        }
    }

    private fun getWebViewPosition(): Int {
        try {
            return mView!!.context.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getInt("webview_position_item=${BookActivity.currentBook.id}_page=${page}", 0)
        } catch (e : java.lang.Exception) {
            return 0
        }
    }

    public fun setDarkTheme() {
        if (mView == null)
            return
        try {
            mView!!.webView.getSettings().setJavaScriptEnabled(true)
            mView!!.webView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"#f0f2f3\");")
            mView!!.webView.loadUrl("javascript:document.body.style.setProperty(\"background-color\", \"#202425\");")
            mView!!.webView.setBackgroundColor(Color.parseColor("#202425"))
        } catch (e : java.lang.Exception) {

        }

    }
    public fun setLightTheme() {
        if (mView == null)
            return
        try {
            mView!!.webView.getSettings().setJavaScriptEnabled(true)
            mView!!.webView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"black\");")
            mView!!.webView.loadUrl("javascript:document.body.style.setProperty(\"background-color\", \"white\");")
            mView!!.webView.setBackgroundColor(resources.getColor(android.R.color.white))
        } catch (e : java.lang.Exception) {

        }

    }

    fun addBookmark () {
        if (mView == null)
            return

        try {
            val realContentHeight = (mView!!.webView.contentHeight * mView!!.webView.scale).toInt()
            val currentScroll = mView!!.webView.scrollY
            Db.addBookmark(BookActivity.currentBook, currentScroll, realContentHeight)
            mView!!.context.longToast(getString(R.string.toast_bookmark_saved))
        } catch (e : java.lang.Exception) {

        }

    }

    private var backgroundTimer: TimerTask? = null
    private fun setupBackgroundWork() {
        try {
            if (context!!.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("item_auto_save_progress", true)) {
                if (backgroundTimer != null) {
                    backgroundTimer!!.cancel()
                    backgroundTimer = null
                }

                backgroundTimer = Timer("background_keeper", false).schedule(15000, 10000) {
                    try {
                        saveWebViewPosition()
                    } catch (e: Exception) {

                    }
                }
            }
        } catch (e : java.lang.Exception) {

        }

    }

    override fun onDestroy() {
        try {
            if (backgroundTimer != null)
                backgroundTimer!!.cancel()
            saveWebViewPosition()
        } catch (e : java.lang.Exception) {

        }

        super.onDestroy()
    }

    override fun onPause() {
        try {
            saveWebViewPosition()
            if (backgroundTimer != null)
                backgroundTimer!!.cancel()
        } catch (e : java.lang.Exception) {

        }

        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        try {
            setupBackgroundWork()
        } catch (e : java.lang.Exception) {

        }
    }

}
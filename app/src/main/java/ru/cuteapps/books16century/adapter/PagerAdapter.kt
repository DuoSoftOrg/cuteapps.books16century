package ru.cuteapps.books16century.adapter

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.cuteapps.books16century.model.Bookmark
import ru.cuteapps.books16century.model.Book
import ru.cuteapps.books16century.widget.PageFragment
import android.util.SparseArray
import android.view.ViewGroup
import collections.forEach
import kotlinx.android.synthetic.main.fragment_page.*
import ru.cuteapps.books16century.BookActivity
import ru.cuteapps.books16century.utils.Helper


/* Created by admin on 17.04.2019. */
class PagerAdapter (fm : FragmentManager) : FragmentStatePagerAdapter (fm) {

    var registeredFragments = SparseArray<PageFragment>()
    var pages = BookActivity.currentBook.pages

    override fun getItem(position: Int): PageFragment {
        return PageFragment.newInstance(position)
    }

    override fun getCount(): Int {
        return pages
    }

    override fun instantiateItem(container: ViewGroup, position: Int): PageFragment {
        val fragment = super.instantiateItem(container, position) as PageFragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    fun getRegisteredFragment(position: Int): PageFragment {
        return registeredFragments.get(position)
    }

    fun minusText() {
        registeredFragments.forEach { i, pageFragment ->
            val settings = pageFragment.webView.settings
            val newZoom = settings.textZoom - 10
            settings.textZoom = newZoom
            pageFragment.context!!.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putInt("webview_zoom", newZoom).apply()
        }
    }

    fun plusText() {
        registeredFragments.forEach { i, pageFragment ->
            val settings = pageFragment.webView.settings
            val newZoom = settings.textZoom + 10
            settings.textZoom = newZoom
            pageFragment.context!!.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putInt("webview_zoom", newZoom).apply()
        }
    }

    fun setLightTheme() {
        registeredFragments.forEach { i, pageFragment ->
            pageFragment.setLightTheme()
        }
    }

    fun setDarkTheme() {
        registeredFragments.forEach { i, pageFragment ->
            pageFragment.setDarkTheme()
        }
    }

}
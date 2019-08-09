package ru.cuteapps.books16century

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_bookmarks.*
import ru.cuteapps.books16century.adapter.BookmarksAdapter
import ru.cuteapps.books16century.model.Book
import ru.cuteapps.books16century.utils.Db
import ru.cuteapps.books16century.utils.Helper

class BookmarksActivity : AppCompatActivity() {

    var item: Book? = null
    var bookId = -1

    companion object {
        const val RESULT_NONE = 0
        const val RESULT_BOOKMARK_SELECTED = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        this.title = "Закладки"

        bookId = intent.getIntExtra("id", -1)
        var adapter : BookmarksAdapter
        if (bookId != -1) {
            this.item = Db.getItem(bookId)
            this.title = "Закладки для '${item!!.title}'"
            adapter = BookmarksAdapter(this, Db.getBookmarks(bookId), false)
        } else {
            this.title = "Закладки"
            adapter = BookmarksAdapter(this, Db.getBookmarks(), false)
        }
        listView.adapter = adapter

        if (getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
            setDarkTheme()
        }
    }

    private fun setDarkTheme() {
        toolbar.setTitleTextColor(resources.getColor(R.color.colorDarkText))
        toolbar.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        listView.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        appbar_l.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        window.statusBarColor = resources.getColor(R.color.colorDarkPrimary)
        window.navigationBarColor = resources.getColor(R.color.colorDarkPrimary)
        window.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorDarkSecondary)))
    }

    private fun setLightTheme() {
        toolbar.setTitleTextColor(resources.getColor(android.R.color.white))
        toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        listView.setBackgroundColor(resources.getColor(R.color.colorGray))
        appbar_l.setBackgroundColor(resources.getColor(android.R.color.white))
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        window.navigationBarColor = resources.getColor(R.color.colorPrimary)
        window.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorGray)))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bookmarks, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

}

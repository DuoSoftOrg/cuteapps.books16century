package ru.cuteapps.books16century

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_last_books.*
import ru.cuteapps.books16century.adapter.LastBooksAdapter
import ru.cuteapps.books16century.utils.Db
import ru.cuteapps.books16century.utils.Helper

class LastBooksActivity : AppCompatActivity() {

    companion object {
        const val RESULT_NONE = 0
        const val RESULT_GO_TO_BOOK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_books)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        this.title = "Последние читаемые книги"

        val adapter = LastBooksAdapter(this, Db.getViewedBooks())
        listView.adapter = adapter

        if (getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
            setDarkTheme()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_last_book, menu)

        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setDarkTheme() {
        toolbar.setTitleTextColor(resources.getColor(R.color.colorDarkText))
        toolbar.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        listView.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        appbar_l.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        listView.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        window.statusBarColor = resources.getColor(R.color.colorDarkPrimary)
        window.navigationBarColor = resources.getColor(R.color.colorDarkPrimary)
    }

    private fun setLightTheme() {
        toolbar.setTitleTextColor(resources.getColor(android.R.color.white))
        toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        listView.setBackgroundColor(resources.getColor(R.color.colorGray))
        appbar_l.setBackgroundColor(resources.getColor(android.R.color.white))
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        window.navigationBarColor = resources.getColor(R.color.colorPrimary)
    }
}

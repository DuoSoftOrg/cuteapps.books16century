package ru.cuteapps.books16century

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_last_books.*
import ru.cuteapps.books16century.adapter.ArchiveAdapter
import ru.cuteapps.books16century.adapter.LastBooksAdapter
import ru.cuteapps.books16century.utils.Db
import ru.cuteapps.books16century.utils.Helper

/* Created by admin on 22.04.2019. */
class ArchiveActivity : AppCompatActivity() {

    companion object {
        const val RESULT_REFRESH = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_books)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        this.title = "Архив"

        val adapter = ArchiveAdapter(this, Db.getArchiveBooks())
        listView.adapter = adapter

        if (getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
            setDarkTheme()
        }

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

}
package ru.cuteapps.books16century

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_fav.*
import ru.cuteapps.books16century.adapter.BooksAdapter
import ru.cuteapps.books16century.adapter.FavAdapter
import ru.cuteapps.books16century.utils.Db
import ru.cuteapps.books16century.utils.Helper

class FavActivity : AppCompatActivity() {

    companion object {
        const val RESULT_NONE = 0
        const val RESULT_GO_TO_BOOK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        title = getString(R.string.title_fav_activity)

        listView.adapter = FavAdapter(this, Db.getFavList())

        if (getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
            setDarkTheme()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return true
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
}

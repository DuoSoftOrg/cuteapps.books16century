package ru.cuteapps.books16century

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_apps.*
import ru.cuteapps.books16century.adapter.AppsAdapter
import ru.cuteapps.books16century.utils.Helper

class AppsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        this.title = "Сборники книг"

        showLoading()
        val adapter = AppsAdapter(this, {
            hideLoading()
        })
        listView.adapter = adapter

        if (getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
            setDarkTheme()
        }
    }

    private fun setDarkTheme() {
        toolbar.setTitleTextColor(resources.getColor(R.color.colorDarkText))
        toolbar.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        listView.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        window.statusBarColor = resources.getColor(R.color.colorDarkPrimary)
        window.navigationBarColor = resources.getColor(R.color.colorDarkPrimary)
        window.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorDarkSecondary)))
    }

    private fun setLightTheme() {
        toolbar.setTitleTextColor(resources.getColor(android.R.color.white))
        toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        listView.setBackgroundColor(resources.getColor(R.color.colorGray))
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        window.navigationBarColor = resources.getColor(R.color.colorPrimary)
        window.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorGray)))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)

    }

    private fun showLoading() {
        progress_l.visibility = View.VISIBLE
        content_l.visibility = View.GONE
    }

    private fun hideLoading() {
        progress_l.visibility = View.GONE
        content_l.visibility = View.VISIBLE
    }
}

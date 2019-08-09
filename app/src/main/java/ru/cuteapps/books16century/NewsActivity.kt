package ru.cuteapps.books16century

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.activity_news.content_l
import kotlinx.android.synthetic.main.activity_news.progress_l
import kotlinx.android.synthetic.main.activity_news.toolbar
import ru.cuteapps.books16century.adapter.NewsAdapter

class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        this.title = "Новости и события"

        val notificationId = intent.getIntExtra("notificationId", -1)
        if (notificationId != -1) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(notificationId)
        }

        showLoading()
        val adapter = NewsAdapter(this, {
            hideLoading()
        })
        listView.adapter = adapter

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

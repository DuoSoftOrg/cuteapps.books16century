package ru.cuteapps.books16century

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_news.*
import android.R.string.cancel
import android.app.NotificationManager
import android.content.Context


class GoAwayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_news)
        //setSupportActionBar(toolbar)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val notificationId = intent.getIntExtra("notificationId", -1)
        if (notificationId != -1) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(notificationId)
        }

        val type = intent.getStringExtra("type")
        if (type != null) {
            if (type == "new_app") {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(intent.getStringExtra("btn1_url"))
                startActivity(i)
                finish()
            }
        }


    }
}

package ru.cuteapps.books16century.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random
import android.graphics.drawable.Drawable
import android.net.Uri
import ru.cuteapps.books16century.GoAwayActivity
import ru.cuteapps.books16century.NewsActivity
import ru.cuteapps.books16century.R


/* Created by admin on 17.06.2019. */
class MessagingService : FirebaseMessagingService() {

    companion object {
        const val NEWS_CHANNEL_ID = "1"
        const val SOCIAL_CHANNEL_ID = "2"

        const val TOPIC_CUTEAPPS_LOVE_BOOKS_NEWS = "cuteapps_love_books_news"

        const val NOTIFICATION_TYPE_NEW_APP = "new_app"
        const val NOTIFICATION_TYPE_SOCIAL = "social"
        const val NOTIFICATION_TYPE_UPDATE = "update"

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d("tag", "Message data payload: " + remoteMessage.data)

        if (!Helper.isRussianLocale(applicationContext))
            return

        val type = remoteMessage.data["type"]
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]
        val btn1_title = remoteMessage.data["btn1_title"]
        val btn1_url = remoteMessage.data["btn1_url"]
        val notificationId = Random.nextInt(1, 777)

        createNewsNotificationChannel(type!!)

        var resultPendingIntent: PendingIntent?

        if (type == NOTIFICATION_TYPE_NEW_APP) {
            val resultIntent = Intent(this, GoAwayActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            resultIntent.putExtra("type", type)
            resultIntent.putExtra("btn1_url", btn1_url)
            resultIntent.putExtra("btn1_title", btn1_title)
            resultIntent.putExtra("notificationId", notificationId)

            resultPendingIntent = PendingIntent.getActivity(
                    this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
           val resultIntent = Intent(this, NewsActivity::class.java)
            resultIntent.putExtra("type", type)
            resultIntent.putExtra("btn1_url", btn1_url)
            resultIntent.putExtra("btn1_title", btn1_title)
            resultIntent.putExtra("notificationId", notificationId)

            resultPendingIntent = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(resultIntent)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }


        val largeIcon = (getDrawable(R.drawable.logo) as BitmapDrawable).bitmap

        val builder = NotificationCompat.Builder(this, NEWS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_book_open)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_apps, remoteMessage.data["btn1_title"], resultPendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }



    }

    private fun createNewsNotificationChannel(type : String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (type == NOTIFICATION_TYPE_SOCIAL) {
                val name = "События в соцсетях"
                val descriptionText = "Уведомления об интересных событиях и постах в наших соцсетях"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(SOCIAL_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            } else {
                val name = "Обновления"
                val descriptionText = "Уведомления о важных обновлениях и свежих релизах"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(NEWS_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

        }
    }

}
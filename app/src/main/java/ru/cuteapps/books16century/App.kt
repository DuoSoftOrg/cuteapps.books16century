package ru.cuteapps.books16century

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import com.google.android.gms.ads.MobileAds
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception
import android.app.ActivityManager
import android.os.Build
import android.util.Log
import android.webkit.WebView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import ru.cuteapps.books16century.shop.Shop
import ru.cuteapps.books16century.utils.Ad
import ru.cuteapps.books16century.utils.Helper
import ru.cuteapps.books16century.utils.MessagingService


class App : Application() {

    companion object {

        var locale = "en"

        private var mContext : Context? = null
        fun getContext () : Context = mContext!!

        fun restart() {

            doAsync {
                Thread.sleep(1000)
                uiThread {
                    try {
                        val mStartActivity = Intent(getContext(), MainActivity::class.java)
                        val mPendingIntentId = 123456
                        val mPendingIntent = PendingIntent.getActivity(getContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT)
                        val mgr = getContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
                        System.exit(0)
                    } catch (ex: Exception) {

                    }
                }
            }

        }

        fun getProcessName(context: Context): String? {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (processInfo in manager.runningAppProcesses) {
                if (processInfo.pid == android.os.Process.myPid()) {
                    return processInfo.processName
                }
            }

            return null
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext

        FirebaseAnalytics.getInstance(this)
        FirebaseApp.initializeApp(this)

        Shop.init()

        if (getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("first_app_launch", true)) {
            Helper.isFirstLaunch = true
            getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("first_app_launch", false).apply()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WebView.setDataDirectorySuffix("work_dir")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = applicationContext.resources.configuration.locales.get(0).language
        } else {
            locale = applicationContext.resources.configuration.locale.language
        }

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("tag", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    // Log and toast
                    Log.d("tag", token)
                })

        FirebaseMessaging.getInstance().subscribeToTopic(MessagingService.TOPIC_CUTEAPPS_LOVE_BOOKS_NEWS)
                .addOnCompleteListener { task ->
                    Log.d("tag", task.isSuccessful.toString() + " - подписка")
                }
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.package_name))
                .addOnCompleteListener { task ->
                    //Log.d("tag", task.isSuccessful.toString() + " - подписка")
                }


    }


}
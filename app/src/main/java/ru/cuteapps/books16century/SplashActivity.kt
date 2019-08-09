package ru.cuteapps.books16century

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.cuteapps.books16century.utils.Db
import ru.cuteapps.books16century.utils.Helper
import ru.cuteapps.books16century.utils.Requests


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BookActivity.isRunning) {
            return
        }

        val intent : Intent
        if (getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("item_load_last_book", true)) {
            val bookId = Db.getLastBookId()
            if (bookId != -1) {
                intent = Intent(this, BookActivity::class.java)
                intent.putExtra("_id", bookId)
                intent.putExtra("fast_load", true)
                startActivityForResult(intent, Requests.BOOK_ACTIVITY_REQUEST)
                finish()
                return
            }
        }
        intent = Intent(this, MainActivity::class.java)
        startActivityForResult(intent, Requests.MAIN_ACTIVITY_REQUEST)
        finish()
    }
}
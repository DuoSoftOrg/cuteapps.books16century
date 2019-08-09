package ru.cuteapps.books16century.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import kotlinx.android.synthetic.main.bookmark_item.view.*
import ru.cuteapps.books16century.BookmarksActivity
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.utils.Db
import ru.cuteapps.books16century.utils.Helper
import org.jetbrains.anko.toast
import ru.cuteapps.books16century.BookActivity
import ru.cuteapps.books16century.utils.Requests
import java.util.*

/* Created by admin on 22.02.2019. */
class BookmarksAdapter (context: Context, var c : Cursor, val fromFragment : Boolean) : CursorAdapter (context, c, 0) {

    fun search (text : String) {
        changeCursor(Db.searchBookmarks(text))
        notifyDataSetChanged()
    }

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.bookmark_item, parent, false)
    }

    override fun bindView(v: View, context: Context, c: Cursor) {

        val strDate = android.text.format.DateFormat.format("dd MMMM yyyyг, HH:mm", Date(c.getLong(c.getColumnIndex("bookmarks_date"))))
        val bookmarkId = c.getInt(c.getColumnIndex("_id"))
        val bookId = c.getInt(c.getColumnIndex("items_id"))
        val page = c.getInt(c.getColumnIndex("bookmarks_page"))
        val pages = c.getInt(c.getColumnIndex("bookmarks_pages"))
        val progress = c.getInt(c.getColumnIndex("bookmarks_progress")).toDouble()
        val pageSize = c.getInt(c.getColumnIndex("bookmarks_page_size")).toDouble()

        val percent = Math.floor(progress / pageSize * 100).toInt()

        //Log.d("tag", "$progress /  $pageSize")

        v.title_tv.text = c.getString(c.getColumnIndex("items_title"))
        v.author_tv.text = c.getString(c.getColumnIndex("items_author"))
        v.date_tv.text = strDate
        v.pages_tv.text = "прочитано $percent% части ${page} из $pages"

        if (context.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
            v.title_tv.setTextColor(context.resources.getColor(R.color.colorDarkText))
            v.author_tv.setTextColor(context.resources.getColor(R.color.colorDarkText))
            v.date_tv.setTextColor(context.resources.getColor(R.color.colorDarkText))
            v.pages_tv.setTextColor(context.resources.getColor(R.color.colorDarkText))
            v.root_view.setBackgroundColor(context.resources.getColor(R.color.colorDarkSecondary))
            v.card_view.setBackgroundColor(context.resources.getColor(R.color.colorDarkPrimary))
        }

        v.main_l.setOnClickListener {
            if (fromFragment) {
                val intent = Intent(context, BookActivity::class.java)
                intent.putExtra("_id", bookId)
                intent.putExtra("bookmark_id", bookmarkId)
                (context as Activity).startActivityForResult(intent, Requests.BOOK_ACTIVITY_REQUEST)
            } else {
                val intent = Intent()
                intent.putExtra("_id", bookId)
                intent.putExtra("bookmark_id", bookmarkId)
                (context as Activity).setResult(BookmarksActivity.RESULT_BOOKMARK_SELECTED, intent)
                (context as Activity).finish()
            }

        }

        v.remove_iv.setOnClickListener {
            Db.exec("delete from bookmarks where _id = $bookmarkId")
            changeCursor(Db.getBookmarks())
            notifyDataSetChanged()
            context.toast("Закладка удалена")
        }

    }

}
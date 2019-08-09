package ru.cuteapps.books16century.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.book_item.view.*
import ru.cuteapps.books16century.LastBooksActivity
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.model.ViewedBook
import ru.cuteapps.books16century.utils.Db
import ru.cuteapps.books16century.utils.Helper
import ru.cuteapps.books16century.utils.loadUrl
import org.jetbrains.anko.longToast
import ru.cuteapps.books16century.BookActivity
import ru.cuteapps.books16century.utils.Requests

/* Created by admin on 24.02.2019. */
class LastBooksAdapter (context: Context, var items : ArrayList<ViewedBook>) : ArrayAdapter<ViewedBook> (context, 0, items) {

    fun search (text : String) {
        this.items.clear()
        this.items.addAll(Db.searchLastBooks(text))
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)

        val book = getItem(position)!!

        v.title_tv.text = book.title
        v.author_tv.text = book.author
        v.image_iv.loadUrl(book.imageUrl, context)
        if (book.page == 0) {
            v.pages_tv.text = "не прочитана"
        } else {
            val page = book.page + 1
            v.pages_tv.text = "часть ${page} из ${book.pages}"
        }

        if (context.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
            v.root_view.setBackgroundColor(context.resources.getColor(R.color.colorDarkPrimary))
            v.card_view.setBackgroundColor(context.resources.getColor(R.color.colorDarkSecondary))
            v.main_l.setBackgroundColor(context.resources.getColor(R.color.colorDarkPrimary))
            v.title_tv.setTextColor(context.resources.getColor(R.color.colorDarkText))
            v.author_tv.setTextColor(context.resources.getColor(R.color.colorDarkText))
            v.pages_tv.setTextColor(context.resources.getColor(R.color.colorDarkText))
        }

        if (book.fav == 0) {
            v.fav_iv.setImageDrawable(context.getDrawable(R.drawable.ic_fav_empty))
        } else {
            v.fav_iv.setImageDrawable(context.getDrawable(R.drawable.ic_fav_full))
        }

        v.main_l.setOnClickListener {

            val intent = Intent(context, BookActivity::class.java)
            intent.putExtra("_id", book.book_id)
            (context as Activity).startActivityForResult(intent, Requests.BOOK_ACTIVITY_REQUEST)

        }

        v.fav_iv.setOnClickListener {

            if (Db.isFav(book.book_id)) {
                Db.setFav(book.book_id, false)
                v.fav_iv.setImageDrawable(context.getDrawable(R.drawable.ic_fav_empty))
                context.longToast(R.string.text_fav_remove)
            } else {
                Db.setFav(book.book_id, true)
                v.fav_iv.setImageDrawable(context.getDrawable(R.drawable.ic_fav_full))
                context.longToast(R.string.text_fav_add)
            }

        }

        v.delete_iv.visibility = View.GONE
        v.download_iv.visibility = View.GONE

        return v
    }

}
package ru.cuteapps.books16century.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.book_item.view.*
import ru.cuteapps.books16century.ArchiveActivity
import ru.cuteapps.books16century.LastBooksActivity
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.model.ArchiveBook
import ru.cuteapps.books16century.model.ViewedBook
import ru.cuteapps.books16century.utils.Db
import ru.cuteapps.books16century.utils.Helper
import ru.cuteapps.books16century.utils.loadUrl
import org.jetbrains.anko.alert
import org.jetbrains.anko.longToast
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class ArchiveAdapter (context: Context, var items : ArrayList<ArchiveBook>) : ArrayAdapter<ArchiveBook>(context, 0, items) {

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
        v.delete_iv.visibility = View.GONE
        v.fav_iv.visibility = View.GONE
        v.download_iv.visibility = View.GONE
        v.restore_iv.visibility = View.VISIBLE

        v.restore_iv.setOnClickListener {
            Db.removeFromArchive(book.book_id)
            this.items.removeAt(position)
            notifyDataSetChanged()
            (context as Activity).setResult(ArchiveActivity.RESULT_REFRESH)
            context.longToast(R.string.text_archive_remove)
        }

        return v
    }
}
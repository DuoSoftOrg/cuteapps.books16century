package ru.cuteapps.books16century.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.book_item.view.*
import ru.cuteapps.books16century.FavActivity
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.model.Book
import org.jetbrains.anko.*
import ru.cuteapps.books16century.BookActivity
import ru.cuteapps.books16century.utils.*

/* Created by admin on 24.02.2019. */
class FavAdapter (context: Context, var items : ArrayList<Book>) : ArrayAdapter<Book> (context, 0, items) {

    fun search (text : String) {
        this.items.clear()
        this.items.addAll(Db.searchFav(text))
        notifyDataSetChanged()
    }

    private fun removeAt(position: Int) {
        Db.addBookToArchive(items[position].id)
        items.removeAt(position)
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
            intent.putExtra("_id", book.id)
            (context as Activity).startActivityForResult(intent, Requests.BOOK_ACTIVITY_REQUEST)

        }

        v.fav_iv.setOnClickListener {

            if (Db.isFav(book.id)) {
                Db.setFav(book.id, false)
                v.fav_iv.setImageDrawable(context.getDrawable(R.drawable.ic_fav_empty))
                context.longToast(R.string.text_fav_remove)
            } else {
                Db.setFav(book.id, true)
                v.fav_iv.setImageDrawable(context.getDrawable(R.drawable.ic_fav_full))
                context.longToast(R.string.text_fav_add)
            }

        }

        v.delete_iv.setOnClickListener {
            context.alert(context.getString(R.string.dialog_archive_add)) {
                //title = "Архив"
                yesButton {
                    it.dismiss()
                    removeAt(position)
                    context.longToast(R.string.text_archive_add)
                }
                noButton {
                    it.dismiss()
                }
            }.show()
        }

        v.download_iv.visibility = View.VISIBLE
        if (book.text.isBlank()) {
            v.download_iv.setOnClickListener {
                if (ContentLoader.bookExists(book.id)) {
                    context.longToast("Книга загружена, теперь вам не понадобится интернет для её чтения")
                    v.download_iv.visibility = View.GONE
                } else {
                    val dialog = context.indeterminateProgressDialog(message = "Секундочку, книга скачивается...", title = "Загрузка")
                    dialog.show()
                    ContentLoader.downloadBook(book.id, {
                        dialog.dismiss()
                        context.longToast("Книга загружена, теперь вам не понадобится интернет для её чтения")
                        v.download_iv.visibility = View.GONE
                    }, { errorMessage ->
                        dialog.dismiss()
                        context.longToast(errorMessage)
                    })
                }
            }
        } else {
            v.download_iv.visibility = View.GONE
        }

        return v
    }

}
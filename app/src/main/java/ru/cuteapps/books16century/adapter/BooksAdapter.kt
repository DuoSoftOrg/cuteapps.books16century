package ru.cuteapps.books16century.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yandex.mobile.ads.AdRequest
import com.yandex.mobile.ads.AdRequestError
import com.yandex.mobile.ads.nativeads.*
import kotlinx.android.synthetic.main.book_item.view.*
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.BookActivity
import ru.cuteapps.books16century.model.Book
import ru.cuteapps.books16century.shop.Shop
import ru.cuteapps.books16century.utils.*
import org.jetbrains.anko.*

/* Created by admin on 17.12.2017. */
class BooksAdapter constructor (var items: ArrayList <Book>?) : RecyclerView.Adapter<BooksAdapter.ViewHolder> () {

    constructor(): this(null) {
        this.items = Db.getAll()
    }

    fun search (text: String) {
        this.items = Db.search(text)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        Db.addBookToArchive(items!![position].id)
        items!!.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Book = items!![position]
        holder.bindItem(item, position, onRemove = {
            removeAt(position)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items!!.size

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem (item: Book, position: Int, onRemove : () -> Unit) = with(itemView) {
            title_tv.text = item.title
            author_tv.text = item.author
            image_iv.loadUrl(item.imageUrl, context)
            if (item.page == 0) {
                pages_tv.text = "не прочитана"
            } else {
                val page = item.page + 1
                pages_tv.text = "часть ${page} из ${item.pages}"
            }

            if (context.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
                root_view.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
                card_view.setBackgroundColor(resources.getColor(R.color.colorDarkSecondary))
                main_l.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
                title_tv.setTextColor(resources.getColor(R.color.colorDarkText))
                author_tv.setTextColor(resources.getColor(R.color.colorDarkText))
                pages_tv.setTextColor(resources.getColor(R.color.colorDarkText))
            }

            if (position == 0 || position % 8 != 0 || Shop.isAdFree) {
                ad_l.visibility = View.GONE
                native_ad.visibility = View.GONE
            } else {
                val adLoaderConfiguration =
                        NativeAdLoaderConfiguration.Builder(resources.getString(R.string.yandex_native), true)
                                .setImageSizes(NativeAdLoaderConfiguration.NATIVE_IMAGE_SIZE_LARGE).build()
                val mNativeAdLoader = NativeAdLoader(context, adLoaderConfiguration)
                mNativeAdLoader.setOnLoadListener(object : NativeAdLoader.OnImageAdLoadListener {
                    override fun onContentAdLoaded(nativeContentAd: NativeContentAd) {
                        ad_l.visibility = View.VISIBLE
                        native_ad.visibility = View.VISIBLE
                        native_ad.setAd(nativeContentAd)
                    }

                    override fun onAppInstallAdLoaded(nativeAppInstallAd: NativeAppInstallAd) {
                        ad_l.visibility = View.VISIBLE
                        native_ad.visibility = View.VISIBLE
                        native_ad.setAd(nativeAppInstallAd)
                    }

                    override fun onAdFailedToLoad(error: AdRequestError) {
                        Log.d("tag", error.description + " " + error.code)
                    }

                    override fun onImageAdLoaded(nativeImageAd: NativeImageAd) {
                        ad_l.visibility = View.VISIBLE
                        native_ad.visibility = View.VISIBLE
                        native_ad.setAd(nativeImageAd)
                    }

                })
                mNativeAdLoader.loadAd(AdRequest.builder().build())
            }

            if (item.fav == 0) {
                fav_iv.setImageDrawable(context.getDrawable(R.drawable.ic_fav_empty))
            } else {
                fav_iv.setImageDrawable(context.getDrawable(R.drawable.ic_fav_full))
            }

            main_l.setOnClickListener { v:View ->

                val intent = Intent(context, BookActivity::class.java)
                intent.putExtra("_id", item.id)
                (context as Activity).startActivityForResult(intent, Requests.BOOK_ACTIVITY_REQUEST)

            }

            fav_iv.setOnClickListener { v ->

                if (Db.isFav(item.id)) {
                    Db.setFav(item.id, false)
                    fav_iv.setImageDrawable(context.getDrawable(R.drawable.ic_fav_empty))
                    context.longToast(R.string.text_fav_remove)
                } else {
                    Db.setFav(item.id, true)
                    fav_iv.setImageDrawable(context.getDrawable(R.drawable.ic_fav_full))
                    context.longToast(R.string.text_fav_add)
                }

            }

            delete_iv.setOnClickListener { v ->
                context.alert(context.getString(R.string.dialog_archive_add)) {
                    //title = "Архив"
                    yesButton {
                        it.dismiss()
                        onRemove()
                        context.longToast(R.string.text_archive_add)
                    }
                    noButton {
                        it.dismiss()
                    }
                }.show()
            }

            download_iv.visibility = View.VISIBLE
            if (item.text.isBlank()) {
                download_iv.setOnClickListener {
                    if (ContentLoader.bookExists(item.id)) {
                        context.longToast("Книга загружена, теперь вам не понадобится интернет для её чтения")
                        download_iv.visibility = View.GONE
                    } else {
                        val dialog = context.indeterminateProgressDialog(message = "Секундочку, книга скачивается...", title = "Загрузка")
                        dialog.show()
                        ContentLoader.downloadBook(item.id, {
                            dialog.dismiss()
                            context.longToast("Книга загружена, теперь вам не понадобится интернет для её чтения")
                            download_iv.visibility = View.GONE
                        }, { errorMessage ->
                            dialog.dismiss()
                            context.longToast(errorMessage)
                        })
                    }
                }
            } else {
                download_iv.visibility = View.GONE
            }


        }

    }



}
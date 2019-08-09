package ru.cuteapps.books16century.main_navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_bookmarks.view.*
import kotlinx.android.synthetic.main.activity_bookmarks.view.listView
import kotlinx.android.synthetic.main.fragment_bookmarks.view.*
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.adapter.BookmarksAdapter
import ru.cuteapps.books16century.utils.Db
import java.lang.Exception

/* Created by admin on 02.05.2019. */
class FragmentBookmarks : Fragment () {

    companion object {
        fun newInstance(): FragmentBookmarks {
            return FragmentBookmarks()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_fav, container, false)

        v.listView.adapter = BookmarksAdapter(context!!, Db.getBookmarks(), true)

        return v
    }


}
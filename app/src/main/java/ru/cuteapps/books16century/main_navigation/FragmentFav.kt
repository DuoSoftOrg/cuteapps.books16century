package ru.cuteapps.books16century.main_navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_fav.view.*
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.adapter.FavAdapter
import ru.cuteapps.books16century.utils.Db

/* Created by admin on 02.05.2019. */
class FragmentFav : Fragment () {

    companion object {
        fun newInstance(): FragmentFav {
            return FragmentFav()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_fav, container, false)

        v.listView.adapter = FavAdapter(context!!, Db.getFavList())

        return v
    }
}
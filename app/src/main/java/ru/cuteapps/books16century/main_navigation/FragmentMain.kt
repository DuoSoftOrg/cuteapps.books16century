package ru.cuteapps.books16century.main_navigation

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.view.*
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.adapter.BooksAdapter
import ru.cuteapps.books16century.shop.Shop
import ru.cuteapps.books16century.utils.Helper


/* Created by admin on 02.05.2019. */
class FragmentMain : Fragment () {

    var mView : View? = null

    companion object {
        fun newInstance(): FragmentMain {
            return FragmentMain()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)

        v.recView.setHasFixedSize(true)
        v.recView.layoutManager = LinearLayoutManager(context)

        v.recView.adapter = BooksAdapter()

        mView = v
        return mView
    }

}
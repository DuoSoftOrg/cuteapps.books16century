package ru.cuteapps.books16century.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.DialogFragment
import kotlinx.android.synthetic.main.page_picker_dialog.view.*
import ru.cuteapps.books16century.R
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.content.Context.INPUT_METHOD_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.view.WindowManager.LayoutParams

import android.content.Context
import android.view.inputmethod.InputMethodManager


/* Created by admin on 20.02.2019. */
class PagePicker  : DialogFragment() {

    lateinit var pagePicker: PagePickerListener
    var currentPage = 1
    var lastPage = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.page_picker_dialog, null)

        v.title_tv.setText("Укажите страницу (1 - $lastPage)")
        v.page_et.setText(currentPage.toString())
        v.page_et.requestFocus()
        try {
            dialog.window.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } catch (e : Exception) {

        }

        v.go_btn.setOnClickListener {
            try {
                var page = v.page_et.text.trim().toString().toInt()
                if (page < 1 || page > lastPage || page == currentPage) {
                    dismiss()
                } else {
                    if (pagePicker != null)
                        pagePicker.pagePicked(page)

                    dismiss()
                }

            } catch (e : Exception) {

            }
        }

        return v
    }

    fun setPages(currentPage : Int, lastPage : Int) {
        this.currentPage = currentPage
        this.lastPage = lastPage
    }

    fun setOnPickListener (pickListener : PagePickerListener) {
        this.pagePicker = pickListener
    }

    interface PagePickerListener {
        fun pagePicked(page : Int)
    }

}
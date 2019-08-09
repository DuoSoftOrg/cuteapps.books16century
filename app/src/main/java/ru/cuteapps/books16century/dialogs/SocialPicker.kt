package ru.cuteapps.books16century.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_social_picker.view.*
import ru.cuteapps.books16century.App
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.utils.Helper

/* Created by admin on 24.04.2019. */
class SocialPicker : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_social_picker, null)

        v.tg_iv.setOnClickListener {
            dismiss()
            Helper.goToTelegram(context!!)
        }
        v.vk_iv.setOnClickListener {
            dismiss()
            Helper.goToVk(context!!)
        }
        v.yt_iv.setOnClickListener {
            dismiss()
            Helper.goToYt(context!!)
        }

        if (App.locale == "ru" || App.locale == "uk" || App.locale == "be") {
            v.yt_iv.visibility = View.VISIBLE
        }

        return v
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (e : Exception) {

        }
    }

}
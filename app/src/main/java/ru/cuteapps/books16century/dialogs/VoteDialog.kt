package ru.cuteapps.books16century.dialogs

import android.app.Activity
import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.utils.Helper

class VoteDialog : DialogFragment(), View.OnClickListener {

    companion object {
        fun showInstance(context: Activity) : Boolean {
            var launchCounter = context.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(Helper.PREF_LAUNCH_COUNTER, 0)
            val voted = context.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(Helper.PREF_VOTED, false)
            if (launchCounter > 6) {
                if (!voted) {
                    val dialog = VoteDialog()
                    dialog.setOnVoteListener (object : VoteDialog.VoteListener {
                        override fun voted() {
                            dialog.dismiss()

                            try {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse("market://details?id=${context.getString(R.string.package_name)}")
                                context.startActivity(intent)
                            } catch (e : Exception) {

                            }
                        }

                        override fun notVoted() {
                            dialog.dismiss()
                        }
                    })
                    dialog.isCancelable = false
                    dialog.show((context as AppCompatActivity).supportFragmentManager, "tag")

                    context.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putBoolean(Helper.PREF_VOTED, true).apply()
                    return true
                }

            } else {
                launchCounter++
                context.getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putInt(Helper.PREF_LAUNCH_COUNTER, launchCounter).apply()
                Log.d("tag", launchCounter.toString() + "")
            }
            return false
        }
    }

    private var voteListener: VoteListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.vote_dialog, null)

        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)

        val star_1 = v?.findViewById<View>(R.id.star_1) as ImageView
        val star_2 = v.findViewById<View>(R.id.star_2) as ImageView
        val star_3 = v.findViewById<View>(R.id.star_3) as ImageView
        val star_4 = v.findViewById<View>(R.id.star_4) as ImageView
        val star_5 = v.findViewById<View>(R.id.star_5) as ImageView
        star_1.setOnClickListener(this)
        star_2.setOnClickListener(this)
        star_3.setOnClickListener(this)
        star_4.setOnClickListener(this)
        star_5.setOnClickListener(this)

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

    override fun onCancel(dialog: DialogInterface) {
        if (voteListener == null) {
            dismiss()
            return
        }
        voteListener?.notVoted()
        super.onCancel(dialog)
    }

    override fun onClick(v: View) {
        if (voteListener == null) {
            dismiss()
            return
        }
        val id = v.id
        when (id) {
            R.id.star_1 -> voteListener?.notVoted()
            R.id.star_2 -> voteListener?.notVoted()
            R.id.star_3 -> voteListener?.notVoted()
            R.id.star_4 -> voteListener?.voted()
            R.id.star_5 -> voteListener?.voted()
        }
    }

    interface VoteListener {
        fun voted()
        fun notVoted()
    }

    fun setOnVoteListener(voteListener: VoteListener) {
        this.voteListener = voteListener
    }



}

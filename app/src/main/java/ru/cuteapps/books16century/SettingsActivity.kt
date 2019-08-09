package ru.cuteapps.books16century

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceActivity
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NavUtils
import ru.cuteapps.books16century.utils.Requests
import ru.cuteapps.books16century.shop.Shop
import ru.cuteapps.books16century.utils.Helper

class SettingsActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.installViewFactory()
        delegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)

        preferenceManager.sharedPreferencesName = Helper.PREFERENCES_NAME
        addPreferencesFromResource(R.xml.pref)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
            setDarkTheme()
        }

        findPreference("theme_dark").setOnPreferenceChangeListener { preference, newValue ->
            setResult(1)
            preference.key
            if (preference.key == "theme_dark" && newValue == true) {
                setDarkTheme()
            } else {
                setLightTheme()
            }
            true
        }

            if (Shop.isAdFree) {
                findPreference("disable_ads").isEnabled = false
            } else {
                findPreference("disable_ads").setOnPreferenceClickListener {
                    try {
                        Shop.buyAdFree(this)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    true
                }
            }

    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this)
            }
            return true
        }
        return super.onMenuItemSelected(featureId, item)
    }

    private fun setDarkTheme() {

        try {
            window.statusBarColor = resources.getColor(R.color.colorDarkPrimary)
            window.navigationBarColor = resources.getColor(R.color.colorDarkPrimary)
            window.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorGray)))
            supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorDarkPrimary)))
        } catch (e : Exception) {

        }
    }

    private fun setLightTheme() {
        try {
            window.statusBarColor = resources.getColor(R.color.colorPrimary)
            window.navigationBarColor = resources.getColor(R.color.colorPrimary)
            window.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorGray)))
            supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        } catch (e : Exception) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Requests.BUY_ADFREE_PRODUCT_REQUEST) {
            val responseCode = data!!.getIntExtra("RESPONSE_CODE", -1)
            if (responseCode == 0) {
                App.restart()
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delegate.onPostCreate(savedInstanceState)
    }

    val supportActionBar: ActionBar?
        get() = delegate.supportActionBar

    fun setSupportActionBar(toolbar: Toolbar?) {
        delegate.setSupportActionBar(toolbar)
    }

    override fun getMenuInflater(): MenuInflater {
        return delegate.menuInflater
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        delegate.setContentView(layoutResID)
    }

    override fun setContentView(view: View) {
        delegate.setContentView(view)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        delegate.setContentView(view, params)
    }

    override fun addContentView(view: View, params: ViewGroup.LayoutParams) {
        delegate.addContentView(view, params)
    }

    override fun onPostResume() {
        super.onPostResume()
        delegate.onPostResume()
    }

    override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        delegate.setTitle(title)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        delegate.onConfigurationChanged(newConfig)
    }

    override fun onStop() {
        super.onStop()
        delegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

    override fun invalidateOptionsMenu() {
        delegate.invalidateOptionsMenu()
    }

    private val delegate: AppCompatDelegate by lazy {
        AppCompatDelegate.create(this, null)
    }
}

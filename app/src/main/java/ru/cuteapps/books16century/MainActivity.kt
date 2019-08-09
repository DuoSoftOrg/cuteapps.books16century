package ru.cuteapps.books16century

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import com.tooltip.Tooltip

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_fav.*
import kotlinx.android.synthetic.main.fragment_main.*
import ru.cuteapps.books16century.adapter.BooksAdapter
import ru.cuteapps.books16century.dialogs.SocialPicker
import ru.cuteapps.books16century.dialogs.VoteDialog
import ru.cuteapps.books16century.utils.Requests
import ru.cuteapps.books16century.shop.Shop
import ru.cuteapps.books16century.utils.Db
import ru.cuteapps.books16century.utils.Helper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import ru.cuteapps.books16century.adapter.BookmarksAdapter
import ru.cuteapps.books16century.adapter.FavAdapter
import ru.cuteapps.books16century.adapter.LastBooksAdapter
import ru.cuteapps.books16century.main_navigation.FragmentBookmarks
import ru.cuteapps.books16century.main_navigation.FragmentFav
import ru.cuteapps.books16century.main_navigation.FragmentLastBooks
import ru.cuteapps.books16century.main_navigation.FragmentMain
import ru.cuteapps.books16century.utils.Ad

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initNavigationDrawer()
        //initRecView()

        bottom_nav.setOnNavigationItemSelectedListener {
            val ft = supportFragmentManager.beginTransaction()
            when (it.itemId) {
                R.id.bottom_nav_home -> {
                    ft.replace(R.id.fl_content, FragmentMain.newInstance(), "FragmentMain")
                    title = getString(R.string.app_name)
                }
                R.id.bottom_nav_fav -> {
                    ft.replace(R.id.fl_content, FragmentFav.newInstance(), "FragmentFav")
                    title = "Избранные книги"
                }
                R.id.bottom_nav_viewed -> {
                    ft.replace(R.id.fl_content, FragmentLastBooks.newInstance(), "FragmentLastBooks")
                    title = "Последние читаемые"
                }
                R.id.bottom_nav_bookmarks -> {
                    ft.replace(R.id.fl_content, FragmentBookmarks.newInstance(), "FragmentBookmarks")
                    title = "Закладки"
                }
            }
            ft.commit()

            true
        }
        bottom_nav.selectedItemId = R.id.bottom_nav_home


        if (getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
            setDarkTheme()
        }

        try {
            Ad.init(applicationContext)
            showFeaturesTooltip()
            showVoteDialog()
        } catch (e : Exception) {

        }

    }

    private fun showFeaturesTooltip() {
        doAsync {

            Thread.sleep(4000)
            uiThread {
                var launchCounter = getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("features_promo_counter", 99)
                Log.d("tag", launchCounter.toString())
                if (launchCounter > 6) {

                    try {
                        Tooltip.Builder(toolbar, R.style.TooltipTG)
                                .setDismissOnClick(true)
                                .setCancelable(true)
                                .setGravity(Gravity.BOTTOM)
                                .setText("В меню появилось много нового!")
                                .setOnClickListener {

                                }
                                .setOnDismissListener {

                                }
                                .show()
                    } catch (e: java.lang.Exception) {

                    }

                    getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putInt("features_promo_counter", 0).apply()
                } else {
                    launchCounter++
                    getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putInt("features_promo_counter", launchCounter).apply()
                }



            }
        }

    }

    private fun loadLastBook() : Boolean {
        val bookId = Db.getLastBookId()
        if (bookId == -1)
            return false
        if (BookActivity.isRunning) {
            return true
        }
        val intent = Intent(this, BookActivity::class.java)
        intent.putExtra("_id", bookId)
        intent.putExtra("fast_load", true)
        startActivityForResult(intent, Requests.BOOK_ACTIVITY_REQUEST)
        return true
    }

    private fun initNavigationDrawer() {
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener (this)

        try {
            if (!Helper.isRussianLocale(this)) {
                nav_view.menu.findItem(R.id.nav_news).isVisible = false
                nav_view.menu.findItem(R.id.nav_share).isVisible = false
                nav_view.menu.findItem(R.id.nav_tg).isVisible = false
                nav_view.menu.findItem(R.id.nav_apps).isVisible = false
            }
        } catch (e : java.lang.Exception) {
            e.printStackTrace()
        }

    }

    private fun showVoteDialog() {
        VoteDialog.showInstance(this)
    }

    private fun initShop() {

        try {
            val menuItem = menu?.findItem(R.id.action_ads)
            menuItem?.isVisible = false
            menuItem?.isEnabled = false
        } catch (e : Exception) {

        }

        if (Shop.isAdFree) {
            try {
                val menuItem = menu?.findItem(R.id.action_ads)
                menuItem?.isVisible = false
                menuItem?.isEnabled = false
            } catch (e: Exception) {

            }
        } else {
            try {
                val menuItem = menu?.findItem(R.id.action_ads)
                menuItem?.isVisible = true
                menuItem?.isEnabled = true
            } catch (e: Exception) {

            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        this.menu = menu

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                try {
                    when (bottom_nav.selectedItemId) {
                        R.id.bottom_nav_home -> {
                            val fragment = supportFragmentManager.findFragmentByTag("FragmentMain")
                            if (fragment != null && fragment.isVisible) {
                                ((fragment as FragmentMain).recView.adapter as BooksAdapter).search(newText)
                            }
                        }
                        R.id.bottom_nav_fav -> {
                            val fragment = supportFragmentManager.findFragmentByTag("FragmentFav")
                            if (fragment != null && fragment.isVisible) {
                                ((fragment as FragmentFav).listView.adapter as FavAdapter).search(newText)
                            }
                        }
                        R.id.bottom_nav_viewed -> {
                            val fragment = supportFragmentManager.findFragmentByTag("FragmentLastBooks")
                            if (fragment != null && fragment.isVisible) {
                                ((fragment as FragmentLastBooks).listView.adapter as LastBooksAdapter).search(newText)
                            }
                        }
                        R.id.bottom_nav_bookmarks -> {
                            val fragment = supportFragmentManager.findFragmentByTag("FragmentBookmarks")
                            if (fragment != null && fragment.isVisible) {
                                ((fragment as FragmentBookmarks).listView.adapter as BookmarksAdapter).search(newText)
                            }
                        }
                    }
                    bottom_nav.selectedItemId
                } catch (e : java.lang.Exception) {

                }

                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

        })

        val vkItem = menu.findItem(R.id.action_vk)
        val tgItem = menu.findItem(R.id.action_tg)
        val socialItem = menu.findItem(R.id.action_socials)
        tgItem.isVisible = false
        vkItem.isVisible = false
        socialItem.isVisible = false

        val ifTg = Helper.tgExists()
        val ifVk = Helper.vkExists()

        if (Helper.isRussianLocale(this)) {
            if (Helper.isFirstLaunch) {
                if (ifTg) {
                    tgItem.isVisible = true
                } else if (ifVk) {
                    vkItem.isVisible = true
                }
            } else {
                socialItem.isVisible = true
            }
        }

        initShop()

        return true
    }

    //@SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                Log.d("tag", "нажат home")
            }
            R.id.action_vk -> {
                Helper.goToVk(this)
            }

            R.id.action_tg -> {
                Helper.goToTelegram(this)
            }

            R.id.action_socials -> {
                val dialog = SocialPicker ()
                dialog.show(supportFragmentManager, "social")
            }
            R.id.action_ads -> {
                try {
                    Shop.buyAdFree(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        /*if (item.itemId == R.id.nav_fav) {
            val int = Intent(this, FavActivity::class.java)
            startActivityForResult(int, Requests.FAV_ACTIVITY_REQUEST)
        } else if (item.itemId == R.id.nav_bookmarks) {
            val intent = Intent(this, BookmarksActivity::class.java)
            intent.putExtra("id", -1)
            startActivityForResult(intent, Requests.BOOKMARKS_ACTIVITY_REQUEST)
        } else if (item.itemId == R.id.nav_last_books) {
            val intent = Intent(this, LastBooksActivity::class.java)
            startActivityForResult(intent, Requests.LASTBOOKS_ACTIVITY_REQUEST)
        }*/

        if (item.itemId == R.id.nav_vote) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("market://details?id=${getString(R.string.package_name)}")
                startActivity(intent)
            } catch (e : Exception) {

            }
        } else if (item.itemId == R.id.nav_share) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=${getString(R.string.package_name)}")
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        } else if (item.itemId == R.id.nav_apps) {
            val intent = Intent(this, AppsActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.nav_news) {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.nav_site) {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://find-books.ru/api/apps/privacy.html")
            startActivity(i)
        } else if (item.itemId == R.id.nav_settings) {
            startActivityForResult(Intent(this, SettingsActivity::class.java), Requests.SETTINGS_ACTIVITY_REQUEST)
        } else if (item.itemId == R.id.nav_archive) {
            val intent = Intent(this, ArchiveActivity::class.java)
            startActivityForResult(intent, Requests.ARCHIVE_ACTIVITY_REQUEST)
        } else if (item.itemId == R.id.nav_disable_ads) {
            try {
                Shop.buyAdFree(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (item.itemId == R.id.nav_tg) {
            SocialPicker().show(supportFragmentManager, "social")
        }

        drawer_layout.closeDrawer(GravityCompat.START)

        return false
    }

    override fun onBackPressed() {
        return super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("tag", resultCode.toString() + " - результат | " + requestCode + " - реквест")

        if (requestCode == Requests.BUY_ADFREE_PRODUCT_REQUEST) {
            val responseCode = data!!.getIntExtra("RESPONSE_CODE", -1)
            if (responseCode == 0) {
                App.restart()
            }
        }
        if (requestCode == Requests.SETTINGS_ACTIVITY_REQUEST && resultCode == 1) {
            if (getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
                setDarkTheme()
            } else {
                setLightTheme()
            }
        }
        if (requestCode == Requests.BOOK_ACTIVITY_REQUEST && Shop.isAdFree) {
            try {
                val menuItem = menu?.findItem(R.id.action_ads)
                menuItem?.isVisible = false
                menuItem?.isEnabled = false
            } catch (e : Exception) {

            }
        }
        if (requestCode == Requests.BOOK_ACTIVITY_REQUEST && resultCode == BookActivity.RESULT_SETTINGS_CHANGES) {
            if (getSharedPreferences(Helper.PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean("theme_dark", false)) {
                setDarkTheme()
            } else {
                setLightTheme()
            }
        }
        if (requestCode == Requests.BOOK_ACTIVITY_REQUEST && resultCode == BookActivity.RESULT_GO_TO_BOOKMARK) {
            val intent = Intent(this, BookActivity::class.java)
            intent.putExtras(data!!)
            startActivityForResult(intent, Requests.BOOK_ACTIVITY_REQUEST)
        }
        if (requestCode == Requests.BOOKMARKS_ACTIVITY_REQUEST && resultCode == BookmarksActivity.RESULT_BOOKMARK_SELECTED) {
            val intent = Intent(this, BookActivity::class.java)
            intent.putExtras(data!!)
            startActivityForResult(intent, Requests.BOOK_ACTIVITY_REQUEST)
        }
        if (requestCode == Requests.LASTBOOKS_ACTIVITY_REQUEST && resultCode == LastBooksActivity.RESULT_GO_TO_BOOK) {
            val intent = Intent(this, BookActivity::class.java)
            intent.putExtras(data!!)
            startActivityForResult(intent, Requests.BOOK_ACTIVITY_REQUEST)
        }
        if (requestCode == Requests.ARCHIVE_ACTIVITY_REQUEST && resultCode == ArchiveActivity.RESULT_REFRESH) {

        }
        if (requestCode == Requests.FAV_ACTIVITY_REQUEST && resultCode == FavActivity.RESULT_GO_TO_BOOK) {
            val intent = Intent(this, BookActivity::class.java)
            intent.putExtras(data!!)
            startActivityForResult(intent, Requests.BOOK_ACTIVITY_REQUEST)
        }
    }

    private fun setDarkTheme() {
        toolbar.setTitleTextColor(resources.getColor(R.color.colorDarkText))
        toolbar.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        //appbar_l.setBackgroundColor(Color.parseColor("#202425"))
        //root_view.setBackgroundColor(Color.parseColor("#202425"))
        //pager_l.setBackgroundColor(Color.parseColor("#202425"))
        window.statusBarColor = resources.getColor(R.color.colorDarkPrimary)
        window.navigationBarColor = resources.getColor(R.color.colorDarkPrimary)
        //fl_content.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        nav_view.setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        nav_view.getHeaderView(0).findViewById<View>(R.id.root_view).setBackgroundColor(resources.getColor(R.color.colorDarkPrimary))
        try {
            val item1 = nav_view.menu.findItem(R.id.nav_head_item_1)
            val item2 = nav_view.menu.findItem(R.id.nav_head_item_2)
            var s1 = SpannableString(item1.getTitle())
            var s2 = SpannableString(item2.getTitle())
            s1.setSpan( TextAppearanceSpan(this, R.style.TextSmallWhite), 0, s1.length, 0)
            s2.setSpan( TextAppearanceSpan(this, R.style.TextSmallWhite), 0, s2.length, 0)
            item1.setTitle(s1)
            item2.setTitle(s2)

        } catch (e : Exception) {

        }

        val colorList = ColorStateList(
                arrayOf(
                        intArrayOf(-android.R.attr.state_enabled),  // Disabled
                        intArrayOf(android.R.attr.state_enabled)    // Enabled
                ),
                intArrayOf(
                        Color.GRAY,     // The color for the Disabled state
                        Color.WHITE       // The color for the Enabled state
                )
        )
        nav_view.itemTextColor = colorList
        nav_view.itemIconTintList = colorList
    }

    private fun setLightTheme() {
        toolbar.setTitleTextColor(resources.getColor(android.R.color.white))
        toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        //appbar_l.setBackgroundColor(resources.getColor(android.R.color.white))
        //root_view.setBackgroundColor(resources.getColor(android.R.color.white))
        //pager_l.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        window.navigationBarColor = resources.getColor(R.color.colorPrimary)
       // fl_content.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
        nav_view.setBackgroundColor(resources.getColor(android.R.color.white))
        nav_view.getHeaderView(0).findViewById<View>(R.id.root_view).setBackgroundColor(resources.getColor(R.color.colorPrimary))
        try {
            val item1 = nav_view.menu.findItem(R.id.nav_head_item_1)
            val item2 = nav_view.menu.findItem(R.id.nav_head_item_2)
            var s1 = SpannableString(item1.getTitle())
            var s2 = SpannableString(item2.getTitle())
            s1.setSpan( TextAppearanceSpan(this, R.style.TextSmallBlack), 0, s1.length, 0)
            s2.setSpan( TextAppearanceSpan(this, R.style.TextSmallBlack), 0, s2.length, 0)
            item1.setTitle(s1)
            item2.setTitle(s2)

        } catch (e : Exception) {

        }

        val colorList = ColorStateList(
                arrayOf(
                        intArrayOf(-android.R.attr.state_enabled),  // Disabled
                        intArrayOf(android.R.attr.state_enabled)    // Enabled
                ),
                intArrayOf(
                        Color.BLACK,     // The color for the Disabled state
                        Color.BLACK       // The color for the Enabled state
                )
        )
        nav_view.itemTextColor = colorList
        nav_view.itemIconTintList = colorList
    }

}

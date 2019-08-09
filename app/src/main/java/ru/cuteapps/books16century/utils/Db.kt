package ru.cuteapps.books16century.utils

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import ru.cuteapps.books16century.App
import ru.cuteapps.books16century.R
import ru.cuteapps.books16century.model.ArchiveBook
import ru.cuteapps.books16century.model.Bookmark
import ru.cuteapps.books16century.model.Book
import ru.cuteapps.books16century.model.ViewedBook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/* Created by admin on 17.12.2017. */
class Db (val context: Context) : SQLiteOpenHelper(context, "db.sqlite", null, 3) {

    init {
        openDataBase()
    }

    companion object {
        private var db: Db? = null
        private fun instance(): Db {
            if (db == null) {
                db = Db(App.getContext())
            }
            return db!!
        }

        fun exec (sql: String) {
            instance().writableDatabase.execSQL(sql)
        }

        fun query (sql: String): Cursor {
            return instance().readableDatabase.rawQuery(sql, null)
        }

        fun isFav (id: Int) : Boolean {
            var isFav = false
            val cursor = Db.query("select * from items where _id = $id")
            if (cursor.moveToNext()) {
                val fav = cursor.getInt(cursor.getColumnIndex("fav"))
                if (fav == 1) {
                    isFav = true
                }
            }
            cursor.close()
            return isFav
        }

        fun setFav(id: Int, addToFav: Boolean) {
            if (addToFav) {
                Db.exec("update items set fav = 1 where _id = $id")
            } else {
                Db.exec("update items set fav = 0 where _id = $id")
            }
        }

        fun getAll () : ArrayList <Book> {
            val items = ArrayList<Book>()
            val q = Db.query("select * from items where _id not in (select book_id from archive_books) order by author asc")
            while (q.moveToNext()) {
                val id = q.getInt(q.getColumnIndex("_id"))
                val title = q.getString(q.getColumnIndex("title"))
                val author = q.getString(q.getColumnIndex("author"))
                val text = q.getString(q.getColumnIndex("text"))
                val textUrl = "${App.getContext().getString(R.string.api_folder)}/$id/textUrl.txt"
                val image = "${App.getContext().getString(R.string.api_folder)}/$id/${q.getString(q.getColumnIndex("image"))}"
                val page = q.getInt(q.getColumnIndex("page"))
                val pages = q.getInt(q.getColumnIndex("pages"))
                val fav = q.getInt(q.getColumnIndex("fav"))
                items.add(Book(id, title, author, text, textUrl, image, fav, page, pages))
            }
            return items
        }

        fun getFavList () : ArrayList <Book> {
            val items = ArrayList<Book>()
            val q = Db.query("select * from items where _id not in (select book_id from archive_books) and fav = 1 order by author asc")
            while (q.moveToNext()) {
                val id = q.getInt(q.getColumnIndex("_id"))
                val title = q.getString(q.getColumnIndex("title"))
                val author = q.getString(q.getColumnIndex("author"))
                val text = q.getString(q.getColumnIndex("text"))
                val textUrl = "${App.getContext().getString(R.string.api_folder)}/$id/textUrl.txt"
                val image = "${App.getContext().getString(R.string.api_folder)}/$id/${q.getString(q.getColumnIndex("image"))}"
                val page = q.getInt(q.getColumnIndex("page"))
                val pages = q.getInt(q.getColumnIndex("pages"))
                val fav = q.getInt(q.getColumnIndex("fav"))
                items.add(Book(id, title, author, text, textUrl, image, fav, page, pages))
            }
            return items
        }

        fun search(searchQuery : String) : ArrayList <Book> {
            val items = ArrayList<Book>()
            val q = Db.query("select * from items where _id not in (select book_id from archive_books) and (title like '%${Helper.escapeSqlParam(searchQuery.toLowerCase())}%' or author like '%${Helper.escapeSqlParam(searchQuery.toLowerCase())}%') order by author asc")
            while (q.moveToNext()) {
                val id = q.getInt(q.getColumnIndex("_id"))
                val title = q.getString(q.getColumnIndex("title"))
                val author = q.getString(q.getColumnIndex("author"))
                val text = q.getString(q.getColumnIndex("text"))
                val textUrl = "${App.getContext().getString(R.string.api_folder)}/$id/textUrl.txt"
                val image = "${App.getContext().getString(R.string.api_folder)}/$id/${q.getString(q.getColumnIndex("image"))}"
                val page = q.getInt(q.getColumnIndex("page"))
                val pages = q.getInt(q.getColumnIndex("pages"))
                val fav = q.getInt(q.getColumnIndex("fav"))
                items.add(Book(id, title, author, text, textUrl, image, fav, page, pages))
            }
            return items
        }

        fun searchFav (searchQuery: String) : ArrayList <Book> {
            val items = ArrayList<Book>()
            val q = Db.query("select * from items where (fav = 1) and (_id not in (select book_id from archive_books)) and (title like '%${Helper.escapeSqlParam(searchQuery.toLowerCase())}%' or author like '%${Helper.escapeSqlParam(searchQuery.toLowerCase())}%') order by author asc")
            while (q.moveToNext()) {
                val id = q.getInt(q.getColumnIndex("_id"))
                val title = q.getString(q.getColumnIndex("title"))
                val author = q.getString(q.getColumnIndex("author"))
                val text = q.getString(q.getColumnIndex("text"))
                val textUrl = "${App.getContext().getString(R.string.api_folder)}/$id/textUrl.txt"
                val image = "${App.getContext().getString(R.string.api_folder)}/$id/${q.getString(q.getColumnIndex("image"))}"
                val page = q.getInt(q.getColumnIndex("page"))
                val pages = q.getInt(q.getColumnIndex("pages"))
                val fav = q.getInt(q.getColumnIndex("fav"))
                items.add(Book(id, title, author, text, textUrl, image, fav, page, pages))
            }
            q.close()
            return items
        }

        fun searchBookmarks (searchQuery: String) : Cursor {
            return Db.query("select bookmarks._id as _id, bookmarks.book_id as bookmarks_book_id, bookmarks.date as bookmarks_date, bookmarks.page as bookmarks_page, bookmarks.pages as bookmarks_pages, bookmarks.progress as bookmarks_progress, bookmarks.page_size as bookmarks_page_size, items._id as items_id, items.title as items_title, items.author as items_author " +
                    "from bookmarks " +
                    "inner join items " +
                    "on bookmarks.book_id = items._id " +
                    "and items.title like '%${Helper.escapeSqlParam(searchQuery.toLowerCase())}%'" +
                    "order by bookmarks.date desc")
        }

        fun searchLastBooks (searchQuery: String) : ArrayList <ViewedBook> {
            val list = ArrayList<ViewedBook>()
            val c = Db.query("select viewed_books._id as _id, viewed_books.book_id as book_id, viewed_books.date as date, viewed_books.count as count, items.title as title, items.author as author, items.genre as genre, items.page as page, items.pages as pages, items.fav as fav, items.poster as poster " +
                    "from viewed_books " +
                    "inner join items " +
                    "on book_id = items._id " +
                    "and items.title like '%${Helper.escapeSqlParam(searchQuery.toLowerCase())}%' " +
                    "order by date desc, count desc")
            while (c.moveToNext()) {
                val bookId = c.getInt(c.getColumnIndex("book_id"))
                val id = c.getInt(c.getColumnIndex("_id"))
                list.add(ViewedBook(
                        id,
                        c.getInt(c.getColumnIndex("book_id")),
                        c.getLong(c.getColumnIndex("date")),
                        c.getInt(c.getColumnIndex("count")),
                        c.getString(c.getColumnIndex("title")),
                        c.getString(c.getColumnIndex("author")),
                        c.getInt(c.getColumnIndex("page")),
                        c.getInt(c.getColumnIndex("pages")),
                        "${App.getContext().getString(R.string.api_folder)}/$id/${c.getString(c.getColumnIndex("image"))}",
                        c.getInt(c.getColumnIndex("fav"))
                ))
            }
            c.close()
            return list
        }

        fun getItem (_id: Int) : Book? {
            var book : Book? = null
            val q = Db.query("select * from items where _id = $_id")
            if (q.moveToNext()) {
                val id = q.getInt(q.getColumnIndex("_id"))
                val title = q.getString(q.getColumnIndex("title"))
                val author = q.getString(q.getColumnIndex("author"))
                val text = q.getString(q.getColumnIndex("text"))
                val textUrl = "${App.getContext().getString(R.string.api_folder)}/$id/textUrl.txt"
                val image = "${App.getContext().getString(R.string.api_folder)}/$id/${q.getString(q.getColumnIndex("image"))}"
                val page = q.getInt(q.getColumnIndex("page"))
                val fav = q.getInt(q.getColumnIndex("fav"))
                val pages = q.getInt(q.getColumnIndex("pages"))
                q.close()
                book = Book(id, title, author, text, textUrl, image, fav, page, pages)
                return book
            }
            q.close()
            return book
        }

        fun getBookmarks() : Cursor {
            return getBookmarks(-1)
        }

        fun getBookmarks(id : Int) : Cursor {
            if (id == -1)
                return Db.query("select bookmarks._id as _id, bookmarks.book_id as bookmarks_book_id, bookmarks.date as bookmarks_date, bookmarks.page as bookmarks_page, bookmarks.pages as bookmarks_pages, bookmarks.progress as bookmarks_progress, bookmarks.page_size as bookmarks_page_size, items._id as items_id, items.title as items_title, items.author as items_author " +
                        "from bookmarks inner join items on bookmarks.book_id = items._id " +
                        "order by bookmarks.date desc")
            else
                return Db.query("select bookmarks._id as _id, bookmarks.book_id as bookmarks_book_id, bookmarks.date as bookmarks_date, bookmarks.page as bookmarks_page, bookmarks.pages as bookmarks_pages, bookmarks.progress as bookmarks_progress, bookmarks.page_size as bookmarks_page_size, items._id as items_id, items.title as items_title, items.author as items_author " +
                        "from bookmarks inner join items on bookmarks.book_id = items._id " +
                        "where items._id = $id " +
                        "order by bookmarks.date desc")
        }

        fun addBookmark(book : Book, progress : Int, pageSize : Int) {
            Db.exec("insert into bookmarks (book_id, date, page, pages, progress, page_size) values " +
                    "(${book.id}, ${Date().time}, ${book.page}, ${book.pages}, ${progress}, ${pageSize})")
        }

        fun getBookmark(id : Int) : Bookmark? {
            var mark : Bookmark?
            val c = Db.query("select * from bookmarks where _id = $id limit 1")
            if (c.moveToNext()) {
                mark = Bookmark(
                        c.getInt(c.getColumnIndex("_id")),
                        c.getLong(c.getColumnIndex("date")),
                        c.getInt(c.getColumnIndex("page")),
                        c.getInt(c.getColumnIndex("pages")),
                        c.getInt(c.getColumnIndex("progress")),
                        c.getInt(c.getColumnIndex("page_size"))
                )
            } else {
                mark = null
            }
            c.close()
            return mark
        }

        /*fun getViewedBook(bookId : Int) : ViewedBook {

        }*/

        fun getViewedBooks() : ArrayList<ViewedBook> {
            val list = ArrayList<ViewedBook>()
            val c = Db.query("select viewed_books._id as _id, viewed_books.book_id as book_id, viewed_books.date as date, viewed_books.count as count, items.title as title, items.author as author, items.page as page, items.pages as pages, items.fav as fav, items.image as image " +
                    "from viewed_books inner join items on book_id = items._id " +
                    "order by date desc, count desc")
            while (c.moveToNext()) {
                val bookId = c.getInt(c.getColumnIndex("book_id"))
                val id = c.getInt(c.getColumnIndex("_id"))
                list.add(ViewedBook(
                        id,
                        c.getInt(c.getColumnIndex("book_id")),
                        c.getLong(c.getColumnIndex("date")),
                        c.getInt(c.getColumnIndex("count")),
                        c.getString(c.getColumnIndex("title")),
                        c.getString(c.getColumnIndex("author")),
                        c.getInt(c.getColumnIndex("page")),
                        c.getInt(c.getColumnIndex("pages")),
                        "${App.getContext().getString(R.string.api_folder)}/$bookId/${c.getString(c.getColumnIndex("image"))}",
                        c.getInt(c.getColumnIndex("fav"))
                ))
            }
            c.close()
            return list
        }

        fun addViewedBook(bookId : Int) {
            val date = Date().time
            val c = Db.query("select * from viewed_books where book_id = $bookId")
            if (c.moveToNext()) {
                var count = c.getInt(c.getColumnIndex("count"))
                count++
                c.close()
                Db.exec("update viewed_books set date = $date, count = $count where book_id = $bookId")

            } else {
                c.close()
                Db.exec("insert into viewed_books (date, book_id) values ($date, $bookId)")
            }
        }

        fun getLastBookId(): Int {
            var id = -1
            val c = Db.query("select book_id from viewed_books order by date desc limit 1")
            if (c.moveToNext()) {
                id = c.getInt(c.getColumnIndex("book_id"))
            }
            c.close()
            return id
        }

        fun getArchiveBooks () : ArrayList<ArchiveBook> {
            val list = ArrayList<ArchiveBook>()
            val c = Db.query("select archive_books._id as _id, archive_books.book_id as book_id, archive_books.date as date, items.title as title, items.author as author, items.page as page, items.pages as pages, items.fav as fav, items.image as image " +
                    "from archive_books inner join items on book_id = items._id " +
                    "order by date desc")
            while (c.moveToNext()) {
                val bookId = c.getInt(c.getColumnIndex("book_id"))
                val id = c.getInt(c.getColumnIndex("_id"))
                list.add(ArchiveBook(
                        id,
                        c.getInt(c.getColumnIndex("book_id")),
                        c.getLong(c.getColumnIndex("date")),
                        c.getString(c.getColumnIndex("title")),
                        c.getString(c.getColumnIndex("author")),
                        c.getInt(c.getColumnIndex("page")),
                        c.getInt(c.getColumnIndex("pages")),
                        "${App.getContext().getString(R.string.api_folder)}/$bookId/${c.getString(c.getColumnIndex("image"))}",
                        c.getInt(c.getColumnIndex("fav"))
                ))
                Log.d( "tag", "id " + bookId)
            }
            c.close()
            return list
        }

        fun addBookToArchive (bookId: Int) {
            val date = Date().time
            val c = Db.query("select * from archive_books where book_id = $bookId")
            if (c.moveToNext()) {
                c.close()
            } else {
                c.close()
                Db.exec("insert into archive_books (date, book_id) values ($date, $bookId)")
            }
        }

        fun removeFromArchive (bookId: Int) {
            Db.exec("delete from archive_books where book_id = $bookId")
            Log.d( "tag", "удалить книу с id $bookId")
        }



    }

    @Throws(SQLException::class)
    fun openDataBase(): SQLiteDatabase {
        val dbFile = context.getDatabasePath("db.sqlite")
        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset()
                println("Copying sucess from Assets folder")
            } catch (e: IOException) {
                throw RuntimeException("Error creating source database", e)
            }

        }
        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS or SQLiteDatabase.CREATE_IF_NECESSARY)
    }

    private fun getDatabasePath(): String {
        return context.getApplicationInfo().dataDir + "/databases/" + "db.sqlite"
    }


    @Throws(IOException::class)
    fun CopyDataBaseFromAsset() {

        val myInput = context.getAssets().open("db.sqlite")
        // Path to the just created empty db
        val outFileName = getDatabasePath()

        var dbFile = File("/assets/db.sqlite")
        // if the path doesn't exist first, create it
        val f = File(context.getApplicationInfo().dataDir + "/databases/")
        if (!f.exists())
            f.mkdir()

        FileOutputStream(outFileName).use { out ->
            context.assets.open("db.sqlite").use {
                it.copyTo(out)
            }
        }

    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table if not exists bookmarks (" +
                "_id integer primary key, " +
                "book_id integer default -1, " +
                "date integer default 0, " +
                "page integer default 1, " +
                "pages integer default 1, " +
                "progress integer default 1, " +
                "page_size integer default 1" +
                ")")
        db.execSQL("create table if not exists viewed_books (_id integer primary key, " +
                "book_id integer default -1, " +
                "date integer default 1, " +
                "count integer default 0" +
                ")")
        db.execSQL("create table if not exists archive_books (_id integer primary key, " +
                "book_id integer default -1, " +
                "date integer default 1 " +
                ")")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("create table if not exists bookmarks (" +
                    "_id integer primary key, " +
                    "book_id integer default -1, " +
                    "date integer default 0, " +
                    "page integer default 1, " +
                    "pages integer default 1, " +
                    "progress integer default 1, " +
                    "page_size integer default 1" +
                    ")")
            db.execSQL("create table if not exists viewed_books (_id integer primary key, " +
                    "book_id integer default -1, " +
                    "date integer default 1, " +
                    "count integer default 0" +
                    ")")
        }
        if (oldVersion < 3) {
            db.execSQL("create table if not exists archive_books (_id integer primary key, " +
                    "book_id integer default -1, " +
                    "date integer default 1 " +
                    ")")
        }

    }

}
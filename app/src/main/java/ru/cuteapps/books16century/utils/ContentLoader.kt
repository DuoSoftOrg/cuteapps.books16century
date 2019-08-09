package ru.cuteapps.books16century.utils

import android.content.Context
import android.util.Log
import ru.cuteapps.books16century.App
import ru.cuteapps.books16century.R
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


object ContentLoader {

    fun bookExists (id : Int) : Boolean {
        return bookExists(id, 0)
    }
    fun bookExists(id: Int, pageIndex : Int): Boolean {
        var bookExists = false
        val c = Db.query("select text from items where _id = $id")
        if (c.moveToNext()) {
            if (!c.getString(c.getColumnIndex("text")).trim().isBlank()) {
                val file = File(c.getString(c.getColumnIndex("text")) + File.separator + pageIndex + ".txt")
                if (file.exists()) {
                    bookExists = true
                } else {
                    Db.exec("update items set text = '' where _id = ${id}")
                }
            }
        }
        c.close()
        return bookExists
    }

    fun downloadBook(id: Int, success: () -> Unit, error: (errorMessage: String) -> Unit) {

        doAsync {

            try {

                val content : String
                val oldFile = File(App.getContext().getDir("content", Context.MODE_PRIVATE).absolutePath + File.separator + "${id}.txt")
                if (oldFile.exists()) {
                    Log.d("tag", "реструкт")
                    content = oldFile.readText()
                } else {
                    val connection = URL("${App.getContext().getString(R.string.api_folder)}/$id/text.txt").openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.doInput = true
                    connection.doOutput = true
                    connection.connect()

                    val reader = BufferedReader(InputStreamReader(BufferedInputStream(connection.inputStream)))
                    content = reader.use(BufferedReader::readText)
                    reader.close()
                }

                var pagesDir = App.getContext().getDir("content", Context.MODE_PRIVATE).absolutePath + File.separator + id.toString() + File.separator
                File(pagesDir).mkdirs()
                val pagesList = content.split("|||")
                for (i in 0 until pagesList.count()) {
                    val pageFile = File(pagesDir + "$i.txt")
                    pageFile.createNewFile()
                    pageFile.writeText(pagesList[i])
                }

                Db.exec("update items set text = '$pagesDir' where _id = ${id}")
                oldFile.delete()

                uiThread {
                    success()
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d("tag", ex.toString())
                uiThread {
                    error("Не удалось загрузить книгу. Скорее всего, сервер сейчас на профилактике, вернитесь чуть позже!")
                }
            }

        }

    }

    fun getPageContentByIndex(id: Int, pageIndex: Int): String {
        var pageContent = ""
        try {
            val c = Db.query("select text from items where _id = $id")
            if (c.moveToNext()) {
                val textLocalPath = c.getString(c.getColumnIndex("text"))
                if (!textLocalPath.trim().isEmpty()) {
                    pageContent = File(textLocalPath + pageIndex + ".txt").readText()
                }
            }
            c.close()
        } catch (e : java.lang.Exception) {

        } catch (e : java.lang.Error) {

        }

        return pageContent
    }

    fun setPageIndex (id: Int, pageIndex: Int) {
        Db.exec("update items set page = $pageIndex where _id = $id")
    }

}
package ru.cuteapps.books16century.model

/* Created by admin on 23.02.2019. */
data class ViewedBook (
        val id : Int,
        val book_id : Int,
        val date : Long,
        var count : Int,
        var title : String,
        var author : String,
        var page : Int,
        var pages : Int,
        val imageUrl:String,
        var fav: Int
)
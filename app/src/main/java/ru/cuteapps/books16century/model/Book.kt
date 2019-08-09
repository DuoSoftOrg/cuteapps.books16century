package ru.cuteapps.books16century.model

/* Created by admin on 17.12.2017. */
data class Book (
        val id:Int,
        val title:String,
        val author:String,
        val text:String,
        val textUrl:String = "",
        val imageUrl:String,
        var fav: Int,
        var page: Int,
        val pages: Int
        )
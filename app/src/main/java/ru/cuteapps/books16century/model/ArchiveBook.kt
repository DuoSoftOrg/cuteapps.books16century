package ru.cuteapps.books16century.model

data class ArchiveBook (
        val id : Int,
        val book_id : Int,
        val date : Long,
        var title : String,
        var author : String,
        var page : Int,
        var pages : Int,
        val imageUrl:String,
        var fav: Int
)
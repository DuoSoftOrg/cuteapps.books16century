package ru.cuteapps.books16century.model

/* Created by admin on 23.02.2019. */
data class Bookmark (
        val id : Int,
        val date : Long,
        val page : Int,
        val pages : Int,
        val progress : Int,
        val page_size : Int
)
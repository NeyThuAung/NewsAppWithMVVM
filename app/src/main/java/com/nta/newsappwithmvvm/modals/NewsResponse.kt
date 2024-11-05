package com.nta.newsappwithmvvm.modals

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)
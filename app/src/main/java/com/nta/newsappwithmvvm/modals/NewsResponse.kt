package com.nta.newsappwithmvvm.modals

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)
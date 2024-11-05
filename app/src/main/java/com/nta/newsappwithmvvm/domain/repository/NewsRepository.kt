package com.nta.newsappwithmvvm.domain.repository

import androidx.lifecycle.LiveData
import com.nta.newsappwithmvvm.modals.Article
import com.nta.newsappwithmvvm.modals.NewsResponse
import retrofit2.Response

interface NewsRepository {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) : Response<NewsResponse>

    suspend fun searchNews(searchQuery : String, pageNumber: Int) : Response<NewsResponse>

    suspend fun upsert(article: Article) : Long

    fun getSavedNews() : LiveData<List<Article>>

    suspend fun deleteArticle(article: Article)

}
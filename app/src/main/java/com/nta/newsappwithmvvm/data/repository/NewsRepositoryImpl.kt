package com.nta.newsappwithmvvm.data.repository

import androidx.lifecycle.LiveData
import com.nta.newsappwithmvvm.modals.NewsResponse
import com.nta.newsappwithmvvm.data.remote.NewsAPI
import com.nta.newsappwithmvvm.db.ArticleDatabase
import com.nta.newsappwithmvvm.domain.repository.NewsRepository
import com.nta.newsappwithmvvm.modals.Article
import retrofit2.Response

class NewsRepositoryImpl(
    private val newsApi: NewsAPI,
    private val db: ArticleDatabase
) : NewsRepository {

    override suspend fun getBreakingNews(
        countryCode: String,
        pageNumber: Int
    ): Response<NewsResponse> {
        return newsApi.getBreakingNews()
    }

    override suspend fun searchNews(searchQuery: String, pageNumber: Int): Response<NewsResponse> {
        return newsApi.getSearchForNews(searchQuery, pageNumber)
    }

    override suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)
    override fun getSavedNews(): LiveData<List<Article>> = db.getArticleDao().getAllArticles()

    override suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}
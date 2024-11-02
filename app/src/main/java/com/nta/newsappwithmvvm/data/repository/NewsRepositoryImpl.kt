package com.nta.newsappwithmvvm.data.repository

import com.nta.newsappwithmvvm.modals.NewsResponse
import com.nta.newsappwithmvvm.data.remote.NewsAPI
import com.nta.newsappwithmvvm.domain.repository.NewsRepository
import retrofit2.Response

class NewsRepositoryImpl(
    private val newsApi : NewsAPI
) : NewsRepository {

    override suspend fun getBreakingNews(
        countryCode: String,
        pageNumber: Int
    ): Response<NewsResponse> {
        return newsApi.getBreakingNews()
    }

}
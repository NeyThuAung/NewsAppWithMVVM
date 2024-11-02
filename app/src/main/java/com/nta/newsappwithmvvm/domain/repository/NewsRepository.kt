package com.nta.newsappwithmvvm.domain.repository

import com.nta.newsappwithmvvm.modals.NewsResponse
import retrofit2.Response

interface NewsRepository {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) : Response<NewsResponse>

}
package com.nta.newsappwithmvvm.data.remote

import com.nta.newsappwithmvvm.modals.NewsResponse
import com.nta.newsappwithmvvm.utils.Constants.Companion.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("/v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode : String = "us",
        @Query("page")
        pageNumber : Int = 1,
        @Query("apikey")
        apiKey : String = API_KEY
    ): retrofit2.Response<NewsResponse>

    @GET("/v2/everything")
    suspend fun getSearchForNews(
        @Query("q")
        searchQuery : String,
        @Query("page")
        pageNumber : Int = 1,
        @Query("apikey")
        apiKey : String = API_KEY
    ): retrofit2.Response<NewsResponse>

}
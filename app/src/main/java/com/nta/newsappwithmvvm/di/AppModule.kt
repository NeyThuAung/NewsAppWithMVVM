package com.nta.newsappwithmvvm.di

import android.app.Application
import com.nta.newsappwithmvvm.data.remote.NewsAPI
import com.nta.newsappwithmvvm.data.repository.NewsRepositoryImpl
import com.nta.newsappwithmvvm.db.ArticleDatabase
import com.nta.newsappwithmvvm.domain.repository.NewsRepository
import com.nta.newsappwithmvvm.utils.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNewsApi(): NewsAPI {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(NewsAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        newsApi: NewsAPI,
        articleDatabase: ArticleDatabase
    ): NewsRepository {
        return NewsRepositoryImpl(newsApi,articleDatabase)
    }

    @Provides
    @Singleton
    fun provideArticleDatabase(application: Application): ArticleDatabase = ArticleDatabase(application)

}
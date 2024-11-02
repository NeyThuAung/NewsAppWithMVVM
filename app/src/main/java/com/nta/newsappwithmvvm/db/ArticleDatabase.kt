package com.nta.newsappwithmvvm.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nta.newsappwithmvvm.api.Converters
import com.nta.newsappwithmvvm.modals.Article
import kotlin.concurrent.Volatile

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao() : ArticleDao

    companion object{

        // Volatile singleton prevents multiple instances of database opening at the same time
        //write variable will be immediately visible to other threads
        @Volatile
        private var instance : ArticleDatabase?= null
        private val LOCK = Any()

        // always call invoke function when everytime create an ArticleDatabase instance
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also{
                instance = it
            }
        }

        //build database
        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }
}
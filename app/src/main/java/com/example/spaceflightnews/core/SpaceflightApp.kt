package com.example.spaceflightnews.core

import android.app.Application
import com.example.spaceflightnews.data.local.dao.ArticleDao
import com.example.spaceflightnews.data.local.database.AppDatabase
import com.example.spaceflightnews.network.retrofit.RetrofitBuilder
import retrofit2.Retrofit

class SpaceflightApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        initRoom()
        initRetrofit()
    }

    private fun initRoom() {
        database = AppDatabase.getInstance(this)
        articleDao = database.articleDao()
    }

    private fun initRetrofit() {
        retrofitBuilder = RetrofitBuilder()
        retrofit = retrofitBuilder.buildRetrofit()
    }

    companion object {
        lateinit var instance: SpaceflightApp
            private set

        lateinit var retrofit: Retrofit
            private set

        lateinit var retrofitBuilder: RetrofitBuilder
            private set

        lateinit var database: AppDatabase
            private set

        lateinit var articleDao: ArticleDao
            private set

    }
}
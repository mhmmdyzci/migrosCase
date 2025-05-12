package com.example.spaceflightnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spaceflightnews.data.local.entity.ArticleEntity

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("SELECT * FROM articles")
    suspend fun getAllArticles(): List<ArticleEntity>

    @Query("DELETE FROM articles")
    suspend fun clearArticles()

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleById(id: Int): ArticleEntity?

    @Query("UPDATE articles SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)

    @Query("SELECT isFavorite FROM articles WHERE id = :id")
    suspend fun isFavorited(id: Int): Boolean

    @Query("SELECT * FROM articles WHERE isFavorite = 1 ORDER BY cachedAt DESC")
    suspend fun getFavoriteArticles(): List<ArticleEntity>
}
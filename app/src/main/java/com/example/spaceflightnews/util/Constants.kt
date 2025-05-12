package com.example.spaceflightnews.util

object Constants {
    const val BASE_URL = "https://api.spaceflightnewsapi.net/v4/"
    object EndPoints {
        const val ARTICLES = "articles"
        const val ARTICLE = "articles/${Paths.ARTICLE_ID}"

    }

    object Queries {
        const val LIMIT = "limit"
        const val OFFSET = "offset"
    }

    object Paths {
        const val ARTICLE_ID = "id"

    }
}
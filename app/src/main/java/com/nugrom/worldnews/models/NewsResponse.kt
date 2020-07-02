package com.nugrom.worldnews.models

import com.nugrom.worldnews.models.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)
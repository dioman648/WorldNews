package com.nugrom.worldnews.models

import com.nugrom.worldnews.models.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)
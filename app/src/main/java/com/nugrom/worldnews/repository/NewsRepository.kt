package com.nugrom.worldnews.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.nugrom.worldnews.api.RetrofitInstance.Companion.api
import com.nugrom.worldnews.db.ArticleDatabase
import com.nugrom.worldnews.models.Article
import com.nugrom.worldnews.util.Constants.Companion.NETWORK_PAGE_SIZE
import kotlinx.coroutines.flow.Flow

class NewsRepository(
    private val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode:String,pageNumber:Int) =
        api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun delete(article: Article) = db.getArticleDao().deleteArticle(article)

    fun getSearchResultStream(query: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = { ArticleSearchPagingSource(query) }
        ).flow
    }

    fun getBreakingNewsStream(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = {ArticlePagingSource()}
        ).flow
    }
}
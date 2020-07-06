package com.nugrom.worldnews.repository

import androidx.paging.PagingSource
import com.nugrom.worldnews.api.RetrofitInstance
import com.nugrom.worldnews.models.Article
import com.nugrom.worldnews.util.Constants.Companion.API_KEY
import com.nugrom.worldnews.util.Constants.Companion.STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.lang.Exception

class ArticleSearchPagingSource(
    private val query: String
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val service = RetrofitInstance.api
        val position = params.key ?: STARTING_PAGE_INDEX
        val apiQuery = query
        return try {
            val response = service.searchForNews(apiQuery, position, API_KEY)
            val articles = response.body()?.articles
            println("mda paging search source $articles")
            LoadResult.Page(
                data = articles!!,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (articles.isEmpty()) null else position + 1
            )
        }catch (exception:Exception){
            return LoadResult.Error(exception)
        }catch (exception:HttpException) {
            return LoadResult.Error(exception)
        }
    }

}


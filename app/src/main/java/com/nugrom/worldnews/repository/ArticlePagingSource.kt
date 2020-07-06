package com.nugrom.worldnews.repository

import androidx.paging.PagingSource
import com.nugrom.worldnews.api.RetrofitInstance
import com.nugrom.worldnews.db.ArticleDatabase
import com.nugrom.worldnews.models.Article
import com.nugrom.worldnews.util.Constants
import retrofit2.HttpException
import java.lang.Exception

class ArticlePagingSource: PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val service = RetrofitInstance.api
        val position = params.key ?: Constants.STARTING_PAGE_INDEX
        return try {
            val response = service.getBreakingNews("ru", position, Constants.API_KEY)
            val articles = response.body()?.articles
            println("mda paging source 3")
            println("mda paging source $articles")
            LoadResult.Page(
                data = articles!!,
                prevKey = if (position == Constants.STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (articles.isEmpty()) null else position + 1
            )
        }catch (exception: Exception){
            return LoadResult.Error(exception)
        }catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}
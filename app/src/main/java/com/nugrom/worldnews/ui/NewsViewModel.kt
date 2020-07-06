package com.nugrom.worldnews.ui

import android.app.DownloadManager
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nugrom.worldnews.models.Article
import com.nugrom.worldnews.models.NewsResponse
import com.nugrom.worldnews.repository.NewsRepository
import com.nugrom.worldnews.util.Resourse
import com.nugrom.worldnews.util.Resourse.Loading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    val newsRepository: NewsRepository
): ViewModel(){

//    val breakingNews : MutableLiveData<Resourse<NewsResponse>> = MutableLiveData()
//    var breakingNewsPage = 1
//

    //Start of CodeLabs Block
    private var currentQueryValue: String? = null

    var currentResult: Flow<PagingData<Article>>? = null
        private set

    var currentSearchResult: Flow<PagingData<Article>>? = null
        private set


    fun searchNews(queryString: String): Flow<PagingData<Article>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult: Flow<PagingData<Article>> = newsRepository.getSearchResultStream(queryString)
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    fun getBreakingNews(): Flow<PagingData<Article>> {
        val newResult: Flow<PagingData<Article>> = newsRepository.getBreakingNewsStream()
            .cachedIn(viewModelScope)
        currentResult = newResult
        return newResult
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }

}
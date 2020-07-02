package com.nugrom.worldnews.ui

import android.app.DownloadManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nugrom.worldnews.models.Article
import com.nugrom.worldnews.models.NewsResponse
import com.nugrom.worldnews.repository.NewsRepository
import com.nugrom.worldnews.util.Resourse
import com.nugrom.worldnews.util.Resourse.Loading
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    val newsRepository: NewsRepository
): ViewModel(){

    val breakingNews : MutableLiveData<Resourse<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResonse: NewsResponse? = null

    val searchNews : MutableLiveData<Resourse<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("ru")
    }

    //viewModelScope живет пока живет текущая view_model
    fun getBreakingNews(countryCode:String) = viewModelScope.launch {
        breakingNews.postValue(Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>):Resourse<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResonse == null) {
                    breakingNewsResonse = resultResponse
                }else{
                    val oldArtcles = breakingNewsResonse?.articles
                    println("mda oldArticles: ${oldArtcles.hashCode()}, breakingNews.articles ${breakingNewsResonse?.articles.hashCode()}")
                    val newArticles = resultResponse.articles
                    oldArtcles?.addAll(newArticles)
                }
                return Resourse.Success(breakingNewsResonse?:resultResponse)
            }
        }
        return Resourse.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>):Resourse<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resourse.Success(resultResponse)
            }
        }
        return Resourse.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }

}
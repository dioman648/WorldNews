package com.nugrom.worldnews.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nugrom.worldnews.R
import com.nugrom.worldnews.adapters.NewsAdapter
import com.nugrom.worldnews.ui.NewsActivity
import com.nugrom.worldnews.ui.NewsViewModel
import com.nugrom.worldnews.util.Constants
import com.nugrom.worldnews.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.nugrom.worldnews.util.Resourse
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment:Fragment(R.layout.fragment_search_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecycleView()

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resourse.Success ->{
                    hideProgressBar()
                    response.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resourse.Error ->{
                    hideProgressBar()
                    response.message?.let {message ->
                        println("mda error: $message")
                    }
                }
                is Resourse.Loading ->{
                    showProgressBar()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setListeners()
    }

    private fun setupRecycleView(){
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = newsAdapter
        }
    }

    fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
    }

    fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
    }

    fun setListeners(){
        newsAdapter.setOnItemClickListener{article->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleNewsFragment,
                bundle
            )
        }

        var job: Job? = null
        etSearch.addTextChangedListener {editable->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                if (!editable.isNullOrBlank()){
                    viewModel.searchNews(editable.toString())
                }
            }
        }
    }

}
package com.nugrom.worldnews.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nugrom.worldnews.R
import com.nugrom.worldnews.adapters.ArticleLoadStateAdapter
import com.nugrom.worldnews.adapters.NewsAdapter
import com.nugrom.worldnews.ui.NewsActivity
import com.nugrom.worldnews.ui.NewsViewModel
import com.nugrom.worldnews.util.Constants.Companion.LAST_SEARCH_QUERY
import com.nugrom.worldnews.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchNewsFragment:Fragment(R.layout.fragment_search_news) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private var searchJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecycleView()


    }

    override fun onResume() {
        super.onResume()
        setListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, etSearch.text?.trim().toString())
    }

    private fun setupRecycleView() {
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = newsAdapter.withLoadStateFooter(
                footer = ArticleLoadStateAdapter { newsAdapter.refresh() }
            )
            this.addItemDecoration(decoration)
            newsAdapter.addLoadStateListener { loadStates ->
                rvSearchNews.isVisible = loadStates.refresh is LoadState.NotLoading
                search_news_progress_bar.isVisible = loadStates.refresh is LoadState.Loading
                search_news_retry_button.isVisible = loadStates.refresh is LoadState.Error

                val errorState = loadStates.source.append as? LoadState.Error
                    ?: loadStates.source.prepend as? LoadState.Error
                    ?: loadStates.append as? LoadState.Error
                    ?: loadStates.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Так: ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            lifecycleScope.launch {
                viewModel.currentSearchResult?.collect {
                    newsAdapter.submitData(it)
                }
            }
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

        etSearch.addTextChangedListener {editable->
            search()
        }

        search_news_retry_button.setOnClickListener {
            newsAdapter.refresh()
        }
    }

    private fun search(){
        val query = etSearch.text?.trim().toString()
        searchJob?.cancel()

        searchJob = lifecycleScope.launch {
            delay(SEARCH_NEWS_TIME_DELAY)
            if (!query.isNullOrBlank()){
                @OptIn(ExperimentalPagingApi::class)
                viewModel.searchNews(query).collectLatest {
                    newsAdapter.submitData(it)
                }
            }
        }
    }






}
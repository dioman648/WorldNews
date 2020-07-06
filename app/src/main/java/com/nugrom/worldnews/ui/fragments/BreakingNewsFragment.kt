package com.nugrom.worldnews.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nugrom.worldnews.R
import com.nugrom.worldnews.adapters.NewsAdapter
import com.nugrom.worldnews.ui.NewsActivity
import com.nugrom.worldnews.ui.NewsViewModel
import com.nugrom.worldnews.util.Resourse
import com.nugrom.worldnews.util.Resourse.*
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BreakingNewsFragment:Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecycleView()

//        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
//             when(response){
//                 is Success ->{
//                     hideProgressBar()
//                     response.data?.let {newsResponse ->
//                         newsAdapter.differ.submitList(newsResponse.articles)
//                     }
//                 }
//                 is Error ->{
//                     hideProgressBar()
//                     response.message?.let {message ->
//                         println("mda error: $message")
//                     }
//                 }
//                 is Loading ->{
//                     showProgressBar()
//                 }
//             }
//        })
        lifecycleScope.launch {
            viewModel.getBreakingNews().collectLatest {
                newsAdapter.submitData(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setListeners()
    }

    private fun setupRecycleView() {
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = newsAdapter
            addItemDecoration(decoration)
        }

        newsAdapter.addLoadStateListener { loadStates ->
            rvBreakingNews.isVisible = loadStates.refresh is LoadState.NotLoading
            breaking_news_progress_bar.isVisible = loadStates.refresh is LoadState.Loading
            breaking_news_retry_button.isVisible = loadStates.refresh is LoadState.Error

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
                R.id.action_breakingNewsFragment_to_articleNewsFragment,
                bundle
            )
        }

        breaking_news_retry_button.setOnClickListener {
            newsAdapter.refresh()
        }
    }
}
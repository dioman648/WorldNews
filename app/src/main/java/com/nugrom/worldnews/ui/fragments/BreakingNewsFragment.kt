package com.nugrom.worldnews.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nugrom.worldnews.R
import com.nugrom.worldnews.adapters.NewsAdapter
import com.nugrom.worldnews.ui.NewsActivity
import com.nugrom.worldnews.ui.NewsViewModel
import com.nugrom.worldnews.util.Resourse
import com.nugrom.worldnews.util.Resourse.*
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment:Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecycleView()

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
             when(response){
                 is Success ->{
                     hideProgressBar()
                     response.data?.let {newsResponse ->
                         newsAdapter.differ.submitList(newsResponse.articles)
                     }
                 }
                 is Error ->{
                     hideProgressBar()
                     response.message?.let {message ->
                         println("mda error: $message")
                     }
                 }
                 is Loading ->{
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
        rvBreakingNews.apply {
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
                R.id.action_breakingNewsFragment_to_articleNewsFragment,
                bundle
            )
        }
    }
}
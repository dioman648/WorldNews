package com.nugrom.worldnews.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.nugrom.worldnews.R
import com.nugrom.worldnews.adapters.NewsAdapter
import com.nugrom.worldnews.ui.NewsActivity
import com.nugrom.worldnews.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleNewsFragment:Fragment(R.layout.fragment_article) {
    lateinit var viewModel: NewsViewModel
    private val args: ArticleNewsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        val article = args.article
        webView.apply {
            //webViewClient = WebViewClient()
            webChromeClient = WebChromeClient().apply {
                settings.javaScriptEnabled = true
            }
            loadUrl(article.url)
        }

        fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view,"Статья сохранена", Snackbar.LENGTH_SHORT).show()
        }
    }
}
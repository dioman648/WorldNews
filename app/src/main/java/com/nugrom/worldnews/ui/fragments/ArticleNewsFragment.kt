package com.nugrom.worldnews.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.nugrom.worldnews.R
import com.nugrom.worldnews.adapters.NewsAdapter
import com.nugrom.worldnews.models.Article
import com.nugrom.worldnews.ui.NewsActivity
import com.nugrom.worldnews.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleNewsFragment:Fragment(R.layout.fragment_article) {
    lateinit var viewModel: NewsViewModel
    private val args: ArticleNewsFragmentArgs by navArgs()
    private lateinit var article: Article

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = (activity as NewsActivity).viewModel
        article = args.article
        println("mda ${article.title} , ${article.url}")
        webView.apply {
            webViewClient = WebViewClient().apply {
                settings.javaScriptEnabled = true
            }
            loadUrl(article.url)
        }

        fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view,"Статья сохранена", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.share ->{
                sendMessage()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendMessage() {
        // Create the text message with a string
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT,article.url)
        }
        val chooser = Intent.createChooser(sendIntent, "World News")//без убрать вариант выбора дефолтного приложения
        // Verify that the intent will resolve to an activity
        if (chooser.resolveActivity(activity?.packageManager!!) != null) {
            startActivity(chooser);
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu,menu)
    }
}
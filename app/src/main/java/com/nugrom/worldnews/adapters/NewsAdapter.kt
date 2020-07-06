package com.nugrom.worldnews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nugrom.worldnews.R
import com.nugrom.worldnews.models.Article
import kotlinx.android.synthetic.main.item_article_preview.view.*

class NewsAdapter: PagingDataAdapter<Article,NewsAdapter.NewsViewHolder>(
    REPO_COMPARATOR
) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Article>(){
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    val differ = AsyncListDiffer(this, REPO_COMPARATOR)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )
    }


    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = getItem(position)
        if (article != null){
            holder.itemView.apply {
                Glide.with(this).load(article.urlToImage).into(ivArticleImage)
                tvSource.text = article.source.name
                tvTitle.text = article.title
                tvDescription.text = article.description
                tvPublishedAt.text = article.publishedAt

                setOnClickListener {
                    onItemClickListener?.let { it(article) }
                }
            }
        }

    }



    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }






}
package com.nugrom.worldnews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.nugrom.worldnews.R
import kotlinx.android.synthetic.main.article_load_state_footer_view_item.view.*

class ArticleLoadStateViewHolder(
    itemView: View,
    retry:() -> Unit
) :RecyclerView.ViewHolder (itemView){


    init {
        itemView.retry_button.also {
            it.setOnClickListener { retry.invoke()}
        }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            itemView.error_msg.text = loadState.error.localizedMessage
        }
        itemView.breakingnews_progress_bar.isVisible = loadState is LoadState.Loading
        itemView.retry_button.isVisible = loadState !is LoadState.Loading
        println("mda loadstate $loadState")
        itemView.error_msg.isVisible = loadState !is LoadState.Loading
        if (loadState is LoadState.Error){
            itemView.retry_button.isVisible = false
        }
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): ArticleLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.article_load_state_footer_view_item, parent, false)
            return ArticleLoadStateViewHolder(view,retry)
        }
    }
}
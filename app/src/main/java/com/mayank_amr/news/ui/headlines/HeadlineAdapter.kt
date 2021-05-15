package com.mayank_amr.news.ui.headlines

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mayank_amr.news.R
import com.mayank_amr.news.data.response.HeadlinesResponse
import com.mayank_amr.news.databinding.HeadlineItemBinding

/**
 * @Project News
 * @Created_by Mayank Kumar on 14-05-2021 01:42 PM
 */
class HeadlineAdapter(private val listener: OnHeadlineItemClickListener) :
        PagingDataAdapter<HeadlinesResponse.Article, HeadlineAdapter.HeadlineViewHolder>(
                HEADLINE_COMPARATOR
        ) {
    private val TAG = "HeadlineAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
        Log.d(TAG, "onCreateViewHolder: ")
        val binding =
                HeadlineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeadlineViewHolder(binding)
    }


    override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ")
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }


    inner class HeadlineViewHolder(private val binding: HeadlineItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
        private val TAG = "HeadlineAdapter"

        init {
            binding.root.setOnClickListener {

                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        if (item.url != null) {
                            listener.onHeadlineItemClick(item.url)
                        }
                    }
                }
            }
        }

        fun bind(headline: HeadlinesResponse.Article) {
            binding.headline = headline

            binding.apply {
                Log.d(TAG, "bind: ")
                if (headline.urlToImage != null) {
                    Glide.with(itemView)
                            .load(headline.urlToImage)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .error(R.drawable.ic_filter)
                            .into(newsImageIv)
                }
            }
        }
    }

    interface OnHeadlineItemClickListener {
        fun onHeadlineItemClick(url: String)
    }


    companion object {
        private val HEADLINE_COMPARATOR =
                object : DiffUtil.ItemCallback<HeadlinesResponse.Article>() {
                    override fun areItemsTheSame(
                            oldItem: HeadlinesResponse.Article,
                            newItem: HeadlinesResponse.Article
                    ) =
                            oldItem.title == newItem.title

                    override fun areContentsTheSame(
                            oldItem: HeadlinesResponse.Article,
                            newItem: HeadlinesResponse.Article
                    ) = oldItem == newItem
                }
    }
}

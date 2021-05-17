package com.mayank_amr.news.ui.headlines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
class HeadlineAdapter(private val listener: OnHeadlineItemClickListener,
                      private val onFavouriteClick: (HeadlinesResponse.Article) -> Unit) :
        PagingDataAdapter<HeadlinesResponse.Article, HeadlineAdapter.HeadlineViewHolder>(
                HEADLINE_COMPARATOR
        ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
        val binding =
                HeadlineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeadlineViewHolder(binding, onFavouriteClick = { position ->
            val article = getItem(position)
            if (article != null) {
                onFavouriteClick(article)
            }
        })
    }


    override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }


    inner class HeadlineViewHolder(private val binding: HeadlineItemBinding, private val onFavouriteClick: (Int) -> Unit) :
            RecyclerView.ViewHolder(binding.root) {

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
            binding.makeFavouriteIv.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFavouriteClick(position)
                }
            }
        }

        fun bind(headline: HeadlinesResponse.Article) {
            binding.headline = headline

            binding.apply {
                if (headline.urlToImage != null) {
                    Glide.with(itemView)
                            .load(headline.urlToImage)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .error(R.drawable.ic_filter)
                            .into(newsImageIv)
                }

                when (headline.isFavourite) {
                    true -> makeFavouriteIv.setColorFilter(ContextCompat.getColor(itemView.context, R.color.red))
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

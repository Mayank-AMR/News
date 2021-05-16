package com.mayank_amr.news.ui.headlines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mayank_amr.news.R
import com.mayank_amr.news.data.response.HeadlinesResponse
import com.mayank_amr.news.databinding.HeadlineItemBinding

/**
 * @Project News
 * @Created_by Mayank Kumar on 16-05-2021 02:46 PM
 */
class ArticleAdapter(
    private val onItemClick: (HeadlinesResponse.Article) -> Unit,
    private val onFavouriteClick: (HeadlinesResponse.Article) -> Unit
) : ListAdapter<HeadlinesResponse.Article, ArticleAdapter.ArticleViewHolder>(ArticleComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            HeadlineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding,
            onItemClick = { position ->
                val article = getItem(position)
                if (article != null) {
                    onItemClick(article)
                }
            },
            onFavouriteClick = { position ->
                val article = getItem(position)
                if (article != null) {
                    onFavouriteClick(article)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    class ArticleViewHolder(
        private val binding: HeadlineItemBinding,
        private val onItemClick: (Int) -> Unit,
        private val onFavouriteClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: HeadlinesResponse.Article) {
            binding.apply {
                headline = article

                Glide.with(itemView)
                    .load(article.urlToImage)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_filter)
                    .into(newsImageIv)

                makeFavouriteButton.text =
                    when {
                        article.isFavourite -> "F"
                        else -> "N"
                    }

            }
        }

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClick(position)
                    }
                }
                makeFavouriteButton.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onFavouriteClick(position)
                    }
                }
            }
        }
    }

    class ArticleComparator : DiffUtil.ItemCallback<HeadlinesResponse.Article>() {

        override fun areItemsTheSame(
            oldItem: HeadlinesResponse.Article,
            newItem: HeadlinesResponse.Article
        ) =
            oldItem.url == newItem.url

        override fun areContentsTheSame(
            oldItem: HeadlinesResponse.Article,
            newItem: HeadlinesResponse.Article
        ) =
            oldItem == newItem
    }
}
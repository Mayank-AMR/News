package com.mayank_amr.news.ui.headlines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mayank_amr.news.databinding.LoadStateFooterBinding
import com.mayank_amr.news.util.visible

/**
 * @Project News
 * @Created_by Mayank Kumar on 15-05-2021 12:39 PM
 */
class HeadlinesLoadStateAdapter(private val retry: () -> Unit) :
        LoadStateAdapter<HeadlinesLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = LoadStateFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return LoadStateViewHolder(binding)
    }


    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class LoadStateViewHolder(private val binding: LoadStateFooterBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonRetryHeadlinesFooter.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                textViewLoadingHeadlinesFooter.visible(loadState is LoadState.Loading)
                progressBarHeadlinesFooter.visible(loadState is LoadState.Loading)
                buttonRetryHeadlinesFooter.visible(loadState !is LoadState.Loading)
                textViewErrorHeadlinesFooter.visible(loadState !is LoadState.Loading)
            }
        }
    }
}
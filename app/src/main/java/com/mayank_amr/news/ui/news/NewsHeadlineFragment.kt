package com.mayank_amr.news.ui.news

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mayank_amr.news.R

class NewsHeadlineFragment : Fragment() {

    companion object {
        fun newInstance() = NewsHeadlineFragment()
    }

    private lateinit var viewModel: NewsHeadlineViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.news_headline_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewsHeadlineViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
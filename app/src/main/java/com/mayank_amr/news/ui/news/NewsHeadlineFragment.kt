package com.mayank_amr.news.ui.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mayank_amr.news.R
import com.mayank_amr.news.data.repository.NewsHeadlineRepository
import com.mayank_amr.news.databinding.NewsHeadlineFragmentBinding
import com.mayank_amr.news.network.Api
import com.mayank_amr.news.network.RemoteDataSource
import com.mayank_amr.news.ui.recyclerview.HeadlineAdapter
import com.mayank_amr.news.viewmodel.NewsHeadlineViewModel
import com.mayank_amr.news.viewmodel.NewsHeadlineViewModelFactory

class NewsHeadlineFragment : Fragment() {
    private val TAG = "NewsHeadlineFragment"

    private var _binding: NewsHeadlineFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.news_headline_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: ")
        super.onViewCreated(view, savedInstanceState)

        _binding = DataBindingUtil.bind(view)

        val adapter = HeadlineAdapter()
        binding.apply {

            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
//            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
//                header = HeadlineStateAdapter { adapter.refresh() },
//                footer = HomePostLoadStateAdapter { adapter.retry() }
//            )
//            buttonPostRetry.setOnClickListener {
//                adapter.retry()
//            }
        }

        // Manually Injected classes instance
        val dataSource = RemoteDataSource()
        val api = dataSource.buildApi(Api::class.java)
        val repository = NewsHeadlineRepository(api)
        val factory = NewsHeadlineViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory).get(NewsHeadlineViewModel::class.java)


        viewModel.headlines.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
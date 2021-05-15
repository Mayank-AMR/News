package com.mayank_amr.news.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.mayank_amr.news.R
import com.mayank_amr.news.data.repository.NewsHeadlineRepository
import com.mayank_amr.news.databinding.NewsHeadlineFragmentBinding
import com.mayank_amr.news.network.Api
import com.mayank_amr.news.network.RemoteDataSource
import com.mayank_amr.news.util.visible
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


        // Manually Injected classes instance
        val dataSource = RemoteDataSource()
        val api = dataSource.buildApi(Api::class.java)
        val repository = NewsHeadlineRepository(api)
        val factory = NewsHeadlineViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory).get(NewsHeadlineViewModel::class.java)


        viewModel.headlines.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                headlineFragmentProgressbar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                headlineFragmentButtonRetry.isVisible = loadState.source.refresh is LoadState.Error
                headlineFragmentErrorResultNotLoadedTv.isVisible = loadState.source.refresh is LoadState.Error

                // for empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        adapter.itemCount < 1
                ) {
                    recyclerView.visible(false)
                    headlineFragmentResultNoResultFoundTv.visible(true)
                } else {
                    headlineFragmentResultNoResultFoundTv.visible(false)
                    // headlineFragmentProgressbar.visible(false)
                }

            }
        }

        binding.apply {

            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = HeadlinesLoadStateAdapter { adapter.refresh() },
                    footer = HeadlinesLoadStateAdapter { adapter.retry() }
            )

            headlineFragmentButtonRetry.setOnClickListener {
                adapter.retry()
            }

            // By Default check Technology chip.
            chipTechnology.isChecked = true

            var previousSelection: Int = chipGroup.checkedChipId //default_selection_id
            chipGroup.setOnCheckedChangeListener { chipGroup, id ->
                if (id == -1) {
                    //Nothing is selected. Now select default Chip
                    chipGroup.check(previousSelection)
                } else {
                    // User selected new chip..
                    previousSelection = id
                    val selectedChipText = chipGroup.findViewById<Chip>(chipGroup.checkedChipId).text.toString()
                    recyclerView.scrollToPosition(0)
                    viewModel.searchHeadlines(selectedChipText)
                }
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.option_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
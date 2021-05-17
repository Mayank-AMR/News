package com.mayank_amr.news.ui.headlines

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.chip.Chip
import com.mayank_amr.news.R
import com.mayank_amr.news.data.db.ArticleDatabase
import com.mayank_amr.news.data.repository.NewsHeadlineRepository
import com.mayank_amr.news.databinding.NewsHeadlineFragmentBinding
import com.mayank_amr.news.network.Api
import com.mayank_amr.news.network.RemoteDataSource
import com.mayank_amr.news.util.visible
import com.mayank_amr.news.viewmodel.NewsHeadlineViewModel
import com.mayank_amr.news.viewmodel.NewsHeadlineViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class NewsHeadlineFragment : Fragment(), HeadlineAdapter.OnHeadlineItemClickListener {
    private val TAG = "NewsHeadlineFragment"

    private var _binding: NewsHeadlineFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewsHeadlineViewModel


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.news_headline_fragment, container, false)
    }


    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: ")
        super.onViewCreated(view, savedInstanceState)

        _binding = DataBindingUtil.bind(view)


        // Manually Injected classes instance
        val dataSource = RemoteDataSource()
        val api = dataSource.buildApi(Api::class.java)
        val repository = NewsHeadlineRepository(api, provideDatabase(requireActivity().application))
        val factory = NewsHeadlineViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(NewsHeadlineViewModel::class.java)

        val adapter = HeadlineAdapter(this, onFavouriteClick = { article ->
            viewModel.onFavouriteClick(article)
        })


        // Loading articles(Headlines) from database
        lifecycleScope.launch {
            viewModel.articleResultFromDB.distinctUntilChanged().collectLatest {
                adapter.submitData(it)
            }
        }

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is NewsHeadlineViewModel.Event.ShowErrorMessage ->
                        Log.e(TAG, "onViewCreated: Could not fetch Data.")
                }
            }
        }

        // Refresh when recyclerview swipe down
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.retry()
        }

        binding.headlineFragmentButtonRetry.setOnClickListener {
            adapter.retry()
        }

        adapter.addLoadStateListener { loadState ->

            Log.d(TAG, "onViewCreated: loadState " + loadState)
            /**
             * CombinedLoadStates(
             * source=LoadStates(refresh=NotLoading(endOfPaginationReached=false), prepend=NotLoading(endOfPaginationReached=false), append=NotLoading(endOfPaginationReached=false)),
             * mediator=LoadStates(refresh=Loading(endOfPaginationReached=false), prepend=NotLoading(endOfPaginationReached=false), append=NotLoading(endOfPaginationReached=false))
             * )
             *
             *  is the loading response of paging library.
             *  Manage the UI according to the "mediator" that is responsible for network call.
             *  Ex:- loadState.mediator!!.refresh is LoadState.Error -- Error loading data from network
             **/


            binding.apply {

                swipeRefreshLayout.isRefreshing = loadState.source.refresh is LoadState.Loading

                //binding.headlineFragmentProgressbar.isVisible = loadState.mediator!!.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading

                //headlineFragmentButtonRetry.isVisible = loadState.mediator!!.refresh is LoadState.Error
                headlineFragmentErrorResultNotLoadedTv.isVisible = loadState.mediator!!.refresh is LoadState.Error

                if (loadState.mediator!!.refresh is LoadState.Loading || loadState.source.refresh is LoadState.Loading) {
                    binding.headlineFragmentProgressbar.visible(true)
                }else{
                    binding.headlineFragmentProgressbar.visible(false)
                }

                if (loadState.mediator!!.refresh is LoadState.Error &&  adapter.itemCount < 1) {
                    // No offline data nad no network.
                    headlineFragmentButtonRetry.visible(true)
                    headlineFragmentErrorResultNotLoadedTv.visible(true)
                }else{
                    headlineFragmentButtonRetry.visible(false)
                    headlineFragmentErrorResultNotLoadedTv.visible(false)
                }

                // for empty view
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
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
            recyclerView.itemAnimator?.changeDuration = 0
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
                    viewModel.searchArticle(selectedChipText)
                }
            }
        }
        setHasOptionsMenu(true)
    }

    //
    override fun onHeadlineItemClick(url: String) {
        val action = NewsHeadlineFragmentDirections.actionNewsHeadlineFragmentToHeadlineDetailFragment(url)
        findNavController().navigate(action)
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

    fun provideDatabase(app: Application): ArticleDatabase =
        Room.databaseBuilder(app, ArticleDatabase::class.java, "article_database")
            .build()


}
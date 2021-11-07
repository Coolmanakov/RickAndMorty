package com.example.rickmorty.feature.episode.presentation

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.example.rickmorty.MyApplication
import com.example.rickmorty.R
import com.example.rickmorty.util.OnItemClicked
import com.example.rickmorty.util.presentation.LoadStateAdapter
import com.example.rickmorty.databinding.FragmentEpisodeBinding
import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto
import com.example.rickmorty.feature.episode.data.remote.model.FilterEpisode
import com.example.rickmorty.feature.episode.presentation.adapters.EpisodeAdapter
import kotlinx.android.synthetic.main.window_episode_filter.view.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class EpisodeFragment : Fragment() {

    @Inject
    lateinit var viewModel: EpisodeViewModel
    private lateinit var binding: FragmentEpisodeBinding
    private lateinit var menuItem: MenuItem
    private lateinit var filter: MenuItem
    private lateinit var searchView: SearchView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_episode, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.bindAdapter(viewModel.episodes)
    }

    private fun FragmentEpisodeBinding.bindAdapter(
        episodes: Flow<PagingData<EpisodeDto>>
    ) {
        val listener = object : OnItemClicked<EpisodeDto> {
            override fun onClick(item: EpisodeDto) {
                val bundle = Bundle().apply {
                    putParcelable(EPISODE_EXTRA, item)
                }
                findNavController().navigate(
                    R.id.action_episodeFragment_to_episodeDetailsFragment,
                    bundle
                )
            }
        }
        val adapter = EpisodeAdapter(listener)
        val header = LoadStateAdapter { adapter.retry() }
        episodeRecycler.adapter = adapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = LoadStateAdapter { adapter.retry() }
        )

        binding.episodeRecyclerRefreshLayout.setOnRefreshListener {
            binding.bindAdapter(episodes)
        }
        bindList(
            header = header,
            adapter = adapter,
            episodes = episodes
        )
    }

    private fun FragmentEpisodeBinding.bindList(
        header: LoadStateAdapter,
        adapter: EpisodeAdapter,
        episodes: Flow<PagingData<EpisodeDto>>,
    ) {
        retryButton.setOnClickListener {
            adapter.retry()
        }

        lifecycleScope.launch {
            episodes
                .distinctUntilChanged()
                .collectLatest {
                    adapter.submitData(it)
                }
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && adapter.itemCount > 0 }
                    ?: loadState.prepend

                episodeRecycler.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                            || loadState.mediator?.refresh is LoadState.NotLoading

                episodeRecyclerRefreshLayout.isRefreshing =
                    loadState.mediator?.refresh is LoadState.Loading

                retryButton.isVisible =
                    loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0

                emptyText.isVisible = adapter.itemCount == 0

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun FragmentEpisodeBinding.bindFilterListener() {
        val filterVisible = !filterEpisode.episodeFilterParams.isVisible
        filterEpisode.episodeFilterParams.isVisible = filterVisible

        filterEpisode.episodeFilterParams.filterEpisodeButton.setOnClickListener {
            startFiltering()
        }
    }

    private fun FragmentEpisodeBinding.startFiltering() {
        val episode =
            filterEpisode.episodeFilterParams.episodeEditTextEpisode.text.toString()
        val name = searchView.query.toString()
        val filterEpisode = FilterEpisode(name, episode)
        val episodes = viewModel.searchFilteredEpisodes(filterEpisode)
        bindAdapter(episodes)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar_menu, menu)
        menuItem = menu.findItem(R.id.search)
        filter = menu.findItem(R.id.filter)

        searchView = menuItem.actionView as SearchView
        with(searchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(episodeName: String?): Boolean {
                    binding.bindAdapter(
                        viewModel.searchFilteredEpisodes(
                            FilterEpisode(
                                name = episodeName,
                                episode = null
                            )
                        )
                    )
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return true
                }
            })
        }
        filter.setOnMenuItemClickListener {
            binding.bindFilterListener()
            return@setOnMenuItemClickListener true
        }
    }

    override fun onStop() {
        menuItem.collapseActionView()
        super.onStop()
    }

    companion object {
        const val EPISODE_EXTRA = "episode"
    }
}
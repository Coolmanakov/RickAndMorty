package com.example.rickmorty.feature.location.presentation

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
import com.example.rickmorty.databinding.FragmentLocationBinding
import com.example.rickmorty.feature.character.data.remote.model.OriginDto
import com.example.rickmorty.feature.location.data.remote.model.FilterLocation
import com.example.rickmorty.feature.location.data.remote.model.LocationDto
import com.example.rickmorty.feature.location.presentation.adapters.LocationAdapter
import kotlinx.android.synthetic.main.window_location_filter.view.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationFragment : Fragment() {

    @Inject
    lateinit var viewModel: LocationViewModel
    private lateinit var binding: FragmentLocationBinding
    private lateinit var menuItem: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var filter: MenuItem

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.bindAdapter(viewModel.locations)
    }


    private fun FragmentLocationBinding.bindAdapter(
        locations: Flow<PagingData<LocationDto>>
    ) {
        val listener = object : OnItemClicked<LocationDto> {
            override fun onClick(item: LocationDto) {
                val bundle = Bundle().apply {
                    //put OriginDto, because we need to make basic schema for transaction from
                    // [CharacterDetailsFragment.kt] and from [LocationFragment.kt]
                    putParcelable(LOCATION_EXTRA, OriginDto(item.name, item.url))
                }
                findNavController().navigate(
                    R.id.action_locationFragment_to_locationDetailsFragment,
                    bundle
                )
            }
        }
        val adapter = LocationAdapter(listener)

        val header = LoadStateAdapter { adapter.retry() }
        locationRecycler.adapter = adapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = LoadStateAdapter { adapter.retry() }
        )
        binding.locationRecyclerRefreshLayout.setOnRefreshListener {
            binding.bindAdapter(locations)
        }
        bindList(
            header = header,
            adapter = adapter,
            locations = locations
        )
    }

    private fun FragmentLocationBinding.bindList(
        header: LoadStateAdapter,
        adapter: LocationAdapter,
        locations: Flow<PagingData<LocationDto>>,
    ) {

        retryButton.setOnClickListener {
            adapter.retry()
        }

        lifecycleScope.launch {
            locations
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

                locationRecycler.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                            || loadState.mediator?.refresh is LoadState.NotLoading

                locationRecyclerRefreshLayout.isRefreshing =
                    loadState.mediator?.refresh is LoadState.Loading

                emptyText.isVisible = adapter.itemCount == 0

                retryButton.isVisible =
                    loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0

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

    private fun FragmentLocationBinding.bindFilterListener() {
        val filterVisibility = !filterLocation.filterLocationParams.isVisible
        filterLocation.filterLocationParams.isVisible = filterVisibility

        filterLocation.filterLocationParams.filterLocationButton.setOnClickListener {
            startFiltering()
        }
    }

    private fun FragmentLocationBinding.startFiltering() {
        val name = searchView.query.toString()
        val type = filterLocation.filterLocationParams.typeEditTextLocation.text.toString()
        val dimension =
            filterLocation.filterLocationParams.dimensionEditTextLocation.text.toString()

        bindAdapter(viewModel.filterLocations(FilterLocation(name, type, dimension)))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar_menu, menu)
        menuItem = menu.findItem(R.id.search)
        filter = menu.findItem(R.id.filter)

        searchView = menuItem.actionView as SearchView
        with(searchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(locationName: String?): Boolean {
                    binding.bindAdapter(
                        viewModel.filterLocations(
                            FilterLocation(
                                name = locationName,
                                type = null,
                                dimension = null
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
        const val LOCATION_EXTRA = "location"
    }
}
package com.example.rickmorty.feature.character.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
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
import com.example.rickmorty.databinding.FragmentCharacterBinding
import com.example.rickmorty.feature.character.data.remote.model.CharacterDto
import com.example.rickmorty.feature.character.data.remote.model.FilterCharacter
import com.example.rickmorty.feature.character.presentation.adapters.CharactersAdapter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@SuppressLint("ResourceType")
class CharacterFragment : Fragment() {
    @Inject
    lateinit var viewModel: CharactersViewModel
    private lateinit var binding: FragmentCharacterBinding
    private lateinit var menuItem: MenuItem
    private lateinit var filter: MenuItem
    private lateinit var searchView: SearchView

    //adapters used for spinners in details window
    private val statusSpinnerAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.statusArray),
        )
    }
    private val genderSpinnerAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.genderArray),
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_character, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //show action bar, without this call we won't have icon in action bar
        setHasOptionsMenu(true)

        binding.bindAdapter(viewModel.characters)
    }

    private fun FragmentCharacterBinding.bindAdapter(
        characters: Flow<PagingData<CharacterDto>>
    ) {
        val characterClickListener = object : OnItemClicked<CharacterDto> {
            override fun onClick(item: CharacterDto) {
                val bundle = Bundle().apply {
                    putParcelable(CHARACTER_EXTRA, item)
                }
                findNavController().navigate(
                    R.id.action_characterFragment_to_characterDetailsFragment,
                    bundle
                )
            }
        }
        val adapter = CharactersAdapter(characterClickListener)
        val header = LoadStateAdapter { adapter.retry() }
        characterRecycler.adapter = adapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = LoadStateAdapter { adapter.retry() }
        )
        //when user pull-to-refresh refresh last adapter list
        binding.characterRecyclerRefreshLayout.setOnRefreshListener {
            binding.bindAdapter(characters)
        }
        bindList(
            header = header,
            adapter = adapter,
            characters = characters
        )
    }

    //Method sets data into RecyclerView
    private fun FragmentCharacterBinding.bindList(
        header: LoadStateAdapter,
        adapter: CharactersAdapter,
        characters: Flow<PagingData<CharacterDto>>,
    ) {
        retryButton.setOnClickListener {
            adapter.retry()
        }

        lifecycleScope.launch {
            characters
                .distinctUntilChanged()
                .collectLatest {
                    adapter.submitData(it)
                }
        }
        //check status of loading to show progress in view
        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && adapter.itemCount > 0 }
                    ?: loadState.prepend

                characterRecycler.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                            || loadState.mediator?.refresh is LoadState.NotLoading

                characterRecyclerRefreshLayout.isRefreshing =
                    loadState.mediator?.refresh is LoadState.Loading

                retryButton.isVisible =
                    loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0

                emptyText.isVisible =
                    loadState.source.refresh is LoadState.NotLoading && adapter.itemCount == 0

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

    //Filter implementation
    @SuppressLint("ResourceType")
    private fun FragmentCharacterBinding.bindFilterListener() {
        val filterVisible = !filterCharacter.filterCharacterParams.isVisible
        filterCharacter.filterCharacterParams.isVisible = filterVisible

        //set adapters for spinners
        statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterCharacter.statusSpinner.adapter = statusSpinnerAdapter
        filterCharacter.genderSpinner.adapter = genderSpinnerAdapter

        //start filtering
        filterCharacter.filterCharacterButton.setOnClickListener {
            startFiltering()
        }
    }

    private fun FragmentCharacterBinding.startFiltering(){
        val status = filterCharacter.statusSpinner.selectedItem.toString()
        val species = filterCharacter.speciesEditText.text.toString()
        val type = filterCharacter.typeEditText.text.toString()
        val gender = filterCharacter.genderSpinner.selectedItem.toString()
        val name = searchView.query.toString()
        val filterCharacter = FilterCharacter(
            name = name,
            status = status,
            species = species,
            type = type,
            gender = gender
        )
        bindAdapter(viewModel.searchFilteredList(filterCharacter))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar_menu, menu)
        menuItem = menu.findItem(R.id.search)
        filter = menu.findItem(R.id.filter)
        searchView = menuItem.actionView as SearchView

        //get input character name, when search is submitted
        with(searchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(characterName: String?): Boolean {
                    binding.bindAdapter(
                        viewModel.searchFilteredList(
                            FilterCharacter(
                                name = characterName,
                                status = null,
                                species = null,
                                type = null,
                                gender = null
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
        const val CHARACTER_EXTRA = "character"
    }
}

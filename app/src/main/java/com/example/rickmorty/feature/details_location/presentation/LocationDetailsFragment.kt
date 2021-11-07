package com.example.rickmorty.feature.details_location.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.example.rickmorty.MyApplication
import com.example.rickmorty.R
import com.example.rickmorty.util.OnItemClicked
import com.example.rickmorty.databinding.FragmentLocationDetailsBinding
import com.example.rickmorty.feature.character.data.remote.model.CharacterDto
import com.example.rickmorty.feature.character.data.remote.model.OriginDto
import com.example.rickmorty.feature.character.presentation.CharacterFragment
import com.example.rickmorty.feature.character.presentation.adapters.CharactersAdapter
import com.example.rickmorty.feature.location.presentation.LocationFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModel: LocationDetailsViewModel
    private val locationParcelable: OriginDto by lazy {
        requireArguments().getParcelable(LocationFragment.LOCATION_EXTRA)!!
    }
    private lateinit var binding: FragmentLocationDetailsBinding
    private lateinit var listener: OnItemClicked<CharacterDto>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_location_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener = object : OnItemClicked<CharacterDto> {
            override fun onClick(item: CharacterDto) {
                val bundle = Bundle().apply {
                    putParcelable(CharacterFragment.CHARACTER_EXTRA, item)
                }
                findNavController().navigate(
                    R.id.action_locationDetailsFragment_to_characterDetailsFragment,
                    bundle
                )
            }
        }

        binding.retry.setOnClickListener {
            startLoading()
        }
        startLoading()
    }

    private fun FragmentLocationDetailsBinding.bindData(
        characters: Flow<PagingData<CharacterDto>>
    ) {
        val adapter = CharactersAdapter(listener)
        characterList.adapter = adapter

        lifecycleScope.launch {
            characters
                .distinctUntilChanged()
                .collectLatest {
                    adapter.submitData(it)
                }
        }
    }

    private fun startLoading() {
        binding.retry.isVisible = false
        binding.progressBar.isVisible = true
        lifecycleScope.launch {
            val location = viewModel.searchSingleLocation(locationParcelable.url)
            if (location == null) {
                Toast.makeText(requireContext(), R.string.ERROR, Toast.LENGTH_LONG).show()
                binding.retry.isVisible = true
                binding.progressBar.isVisible = false
            } else {
                binding.progressBar.isVisible = false
                binding.location = location.first
                binding.bindData(location.second)
            }
        }
    }
}


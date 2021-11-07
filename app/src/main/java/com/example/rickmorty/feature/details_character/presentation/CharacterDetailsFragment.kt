package com.example.rickmorty.feature.details_character.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.bumptech.glide.Glide
import com.example.rickmorty.MyApplication
import com.example.rickmorty.R
import com.example.rickmorty.util.OnItemClicked
import com.example.rickmorty.databinding.FragmentCharacterDetailsBinding
import com.example.rickmorty.feature.character.data.remote.model.CharacterDto
import com.example.rickmorty.feature.character.presentation.CharacterFragment
import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto
import com.example.rickmorty.feature.episode.presentation.adapters.EpisodeAdapter
import com.example.rickmorty.feature.location.presentation.LocationFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharacterDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModel: CharacterDetailsViewModel
    private lateinit var binding: FragmentCharacterDetailsBinding
    private lateinit var listener: OnItemClicked<EpisodeDto>

    private val characterParcelable: CharacterDto by lazy {
        requireArguments().getParcelable(CharacterFragment.CHARACTER_EXTRA)!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_character_details,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener = object : OnItemClicked<EpisodeDto> {
            override fun onClick(item: EpisodeDto) {
                val bundle = Bundle().apply {
                    putParcelable(EPISODE_EXTRA, item)
                }
                findNavController().navigate(
                    R.id.action_characterDetailsFragment_to_episodeDetailsFragment,
                    bundle
                )
            }
        }
        binding.bindData(viewModel.searchEpisodesByIds(getIdList()))
    }

    private fun FragmentCharacterDetailsBinding.bindData(
        episodes: Flow<PagingData<EpisodeDto>>
    ) {
        val adapter = EpisodeAdapter(listener)
        episodeList.adapter = adapter

        character = characterParcelable
        Glide.with(requireContext()).load(characterParcelable.image).into(binding.image)

        lifecycleScope.launch {
            episodes
                .distinctUntilChanged()
                .collectLatest {
                    adapter.submitData(it)
                }
        }
        bindListeners()
    }

    private fun FragmentCharacterDetailsBinding.bindListeners() {
        origin.setOnClickListener {
            if (characterParcelable.origin.name != "unknown") {
                val bundle = Bundle().apply {
                    putParcelable(
                        LocationFragment.LOCATION_EXTRA,
                        characterParcelable.origin
                    )
                }
                findNavController().navigate(
                    R.id.action_characterDetailsFragment_to_locationDetailsFragment,
                    bundle
                )
            }
        }

        location.setOnClickListener {
            if (characterParcelable.characterLocation.name != "unknown") {
                val bundle = Bundle().apply {
                    putParcelable(
                        LocationFragment.LOCATION_EXTRA,
                        characterParcelable.characterLocation
                    )
                }
                findNavController().navigate(
                    R.id.action_characterDetailsFragment_to_locationDetailsFragment,
                    bundle
                )
            }
        }
    }

    private fun getIdList(): List<Int> {
        return characterParcelable.episode.map {
            it.substringAfterLast("/").toInt()
        }
    }
    companion object {
        const val EPISODE_EXTRA = "episode"
    }
}



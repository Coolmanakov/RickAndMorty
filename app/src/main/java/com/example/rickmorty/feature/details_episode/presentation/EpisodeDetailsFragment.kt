package com.example.rickmorty.feature.details_episode.presentation

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
import com.example.rickmorty.MyApplication
import com.example.rickmorty.R
import com.example.rickmorty.util.OnItemClicked
import com.example.rickmorty.databinding.FragmentEpisodeDetailsBinding
import com.example.rickmorty.feature.character.data.remote.model.CharacterDto
import com.example.rickmorty.feature.character.presentation.CharacterFragment
import com.example.rickmorty.feature.character.presentation.adapters.CharactersAdapter
import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto
import com.example.rickmorty.feature.episode.presentation.EpisodeFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class EpisodeDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModel: EpisodeDetailsViewModel

    private val episodeParcelable: EpisodeDto by lazy {
        requireArguments().getParcelable(EpisodeFragment.EPISODE_EXTRA)!!
    }
    private lateinit var binding: FragmentEpisodeDetailsBinding
    private lateinit var listener: OnItemClicked<CharacterDto>

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
                R.layout.fragment_episode_details,
                container,
                false
            )

        listener = object : OnItemClicked<CharacterDto> {
            override fun onClick(item: CharacterDto) {
                val bundle = Bundle().apply {
                    putParcelable(CharacterFragment.CHARACTER_EXTRA, item)
                }
                findNavController().navigate(
                    R.id.action_episodeDetailsFragment_to_characterDetailsFragment,
                    bundle
                )
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bindData(viewModel.searchCharactersByIds(getIdList()))
    }

    private fun FragmentEpisodeDetailsBinding.bindData(
        characters: Flow<PagingData<CharacterDto>>
    ) {
        episode = episodeParcelable

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

    private fun getIdList(): List<Int> {
        return episodeParcelable.characters.map {
            it.substringAfterLast("/").toInt()
        }
    }
}
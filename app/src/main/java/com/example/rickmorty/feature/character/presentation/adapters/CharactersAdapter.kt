package com.example.rickmorty.feature.character.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.rickmorty.util.OnItemClicked
import com.example.rickmorty.R
import com.example.rickmorty.feature.character.data.remote.model.CharacterDto

class CharactersAdapter(private val characterClickListener: OnItemClicked<CharacterDto>) :
    PagingDataAdapter<CharacterDto, CharacterViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder =
        CharacterViewHolder(
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_character, parent, false),
            characterClickListener = characterClickListener
        )

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = getItem(position)
        holder.bind(character)

    }


    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<CharacterDto>() {
            override fun areItemsTheSame(oldItem: CharacterDto, newItem: CharacterDto): Boolean {
                return (oldItem.name == newItem.name)
            }

            override fun areContentsTheSame(oldItem: CharacterDto, newItem: CharacterDto): Boolean =
                oldItem == newItem

        }
    }
}


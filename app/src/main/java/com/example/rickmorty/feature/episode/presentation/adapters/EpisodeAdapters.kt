package com.example.rickmorty.feature.episode.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.rickmorty.R
import com.example.rickmorty.util.OnItemClicked
import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto

class EpisodeAdapter(private val listener: OnItemClicked<EpisodeDto>) :
    PagingDataAdapter<EpisodeDto, EpisodeViewHolder>(COMPARATOR) {

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = getItem(position)
        holder.bind(episode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder =
        EpisodeViewHolder(
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_episode, parent, false),
            listener = listener
        )



    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<EpisodeDto>() {
            override fun areItemsTheSame(oldItem: EpisodeDto, newItem: EpisodeDto): Boolean {
                return (oldItem.name == newItem.name)
            }

            override fun areContentsTheSame(oldItem: EpisodeDto, newItem: EpisodeDto): Boolean =
                oldItem == newItem

        }
    }
}
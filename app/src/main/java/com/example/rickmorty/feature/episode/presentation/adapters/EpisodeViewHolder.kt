package com.example.rickmorty.feature.episode.presentation.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rickmorty.R
import com.example.rickmorty.util.OnItemClicked
import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto

class EpisodeViewHolder(
    private val view: View,
    private val listener: OnItemClicked<EpisodeDto>
) : RecyclerView.ViewHolder(view) {

    private val episodeName = view.findViewById<TextView>(R.id.location_name)
    private val episodeDigit = view.findViewById<TextView>(R.id.type)
    private val episodeDate = view.findViewById<TextView>(R.id.dimension)



    fun bind(episode: EpisodeDto?) {
        if (episode != null) {
            episodeName.text = episode.name
            episodeDigit.text = episode.episode
            episodeDate.text = episode.air_date

            view.setOnClickListener {
                listener.onClick(episode)
            }
        }
    }
}

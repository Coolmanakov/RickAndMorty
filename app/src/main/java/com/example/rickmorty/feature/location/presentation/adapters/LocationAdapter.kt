package com.example.rickmorty.feature.location.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.rickmorty.R
import com.example.rickmorty.util.OnItemClicked
import com.example.rickmorty.feature.location.data.remote.model.LocationDto

class LocationAdapter(private val listener: OnItemClicked<LocationDto>) :
    PagingDataAdapter<LocationDto, LocationViewHolder>(COMPARATOR) {

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = getItem(position)
        holder.bind(location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder =
        LocationViewHolder(
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_location, parent, false),
            listener = listener
        )


    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<LocationDto>() {
            override fun areItemsTheSame(oldItem: LocationDto, newItem: LocationDto): Boolean {
                return (oldItem.name == newItem.name)
            }

            override fun areContentsTheSame(oldItem: LocationDto, newItem: LocationDto): Boolean =
                oldItem == newItem

        }
    }
}
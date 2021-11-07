package com.example.rickmorty.feature.location.presentation.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rickmorty.R
import com.example.rickmorty.util.OnItemClicked
import com.example.rickmorty.feature.location.data.remote.model.LocationDto

class LocationViewHolder(
    private val view: View,
    private val listener: OnItemClicked<LocationDto>
) : RecyclerView.ViewHolder(view) {

    private val locationName = view.findViewById<TextView>(R.id.location_name)
    private val type = view.findViewById<TextView>(R.id.type)
    private val dimension = view.findViewById<TextView>(R.id.dimension)

    fun bind(location: LocationDto?) {
        if (location != null) {
            locationName.text = location.name
            type.text = location.type
            dimension.text = location.dimension

            view.setOnClickListener {
                listener.onClick(location)
            }
        }
    }
}

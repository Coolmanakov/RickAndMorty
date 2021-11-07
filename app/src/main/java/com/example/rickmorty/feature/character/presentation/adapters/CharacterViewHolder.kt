package com.example.rickmorty.feature.character.presentation.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickmorty.R
import com.example.rickmorty.feature.character.data.remote.model.CharacterDto
import com.example.rickmorty.util.OnItemClicked

class CharacterViewHolder(
    private val view: View,
    private val characterClickListener: OnItemClicked<CharacterDto>
) : RecyclerView.ViewHolder(view) {

    private val nameCharacter = view.findViewById<TextView>(R.id.nameCharacter)
    private val species = view.findViewById<TextView>(R.id.species)
    private val status = view.findViewById<TextView>(R.id.status)
    private val gender = view.findViewById<TextView>(R.id.gender)
    private val image = view.findViewById<ImageView>(R.id.character_image)

    fun bind(character: CharacterDto?) {
        if (character != null) {
            nameCharacter.text = character.name
            species.text = character.species
            status.text = character.status
            gender.text = character.gender
            Glide.with(view).load(character.image).into(image)

            view.setOnClickListener {
                characterClickListener.onClick(character)
            }
        }
    }

}

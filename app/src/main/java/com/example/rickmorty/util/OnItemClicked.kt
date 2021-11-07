package com.example.rickmorty.util

interface OnItemClicked<T> {

    fun onClick(item: T)
}
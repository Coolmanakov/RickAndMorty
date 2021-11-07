package com.example.rickmorty.util.model

data class InfoResponseDto(
    val count: String,
    val pages: String,
    val next: String?,
    val prev: String?,
)

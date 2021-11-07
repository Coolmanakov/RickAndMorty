package com.example.rickmorty.retrofit

import com.example.rickmorty.db.RickMortyDatabase
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor.Level

object RetrofitUtils {

    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    @Volatile
    private var INSTANCE: Retrofit? = null

    fun <T> createService(service: Class<T>): T {
        return getInstance().create(service)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(buildGson()))
            .client(buildHttpClient())
            .build()
    }
    private fun getInstance() : Retrofit{
        return INSTANCE ?: synchronized(this){
            INSTANCE ?: getRetrofit().also { INSTANCE = it }
        }
    }

    private fun buildHttpClient() = OkHttpClient.Builder()
        .addInterceptor(buildLogger())
        .build()

    private fun buildLogger() = HttpLoggingInterceptor()
        .setLevel(Level.BASIC)

    private fun buildGson() = GsonBuilder()
        .create()

}
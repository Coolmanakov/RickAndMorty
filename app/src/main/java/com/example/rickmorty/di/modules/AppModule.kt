package com.example.rickmorty.di.modules

import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun getListOfIntegers(): List<Int>{
        return emptyList()
    }
}
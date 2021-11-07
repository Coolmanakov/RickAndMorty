package com.example.rickmorty

import android.app.Application
import com.example.rickmorty.di.AppComponent
import com.example.rickmorty.di.DaggerAppComponent

class MyApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}
package com.example.watchit

import android.app.Application
import android.content.Context

class WatchItApplication : Application() {
    
    object Globals {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.appContext = applicationContext
    }
}
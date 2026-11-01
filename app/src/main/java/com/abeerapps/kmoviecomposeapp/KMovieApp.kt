package com.abeerapps.kmoviecomposeapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class KMovieApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Release builds plant no tree, so nothing is written to logcat.
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}

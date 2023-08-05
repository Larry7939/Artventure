package com.artventure.artventure.application

import android.app.Application
import com.artventure.artventure.BuildConfig
import timber.log.Timber


class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

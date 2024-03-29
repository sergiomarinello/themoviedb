package com.smarinello.themoviedb

import android.app.Application
import android.content.Context
import com.smarinello.themoviedb.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Dependency injection on app.
 */
class TheMovieDbApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        startKoin {
            // logger for koin
            androidLogger()
            // declare used Android context
            androidContext(this@TheMovieDbApplication)
            // declare modules
            modules(appComponent)
        }
    }
}

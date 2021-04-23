package com.smarinello.themoviedb.di

import androidx.room.Room
import com.smarinello.themoviedb.repository.Repository
import com.smarinello.themoviedb.repository.local.AppDatabase
import com.smarinello.themoviedb.repository.remote.MovieRepository
import com.smarinello.themoviedb.repository.remote.RemoteAccess
import com.smarinello.themoviedb.utils.ConnectivityUtils
import com.smarinello.themoviedb.viewmodel.DetailActivityViewModel
import com.smarinello.themoviedb.viewmodel.MainActivityViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.lang.ref.WeakReference

/**
 * Android application context module.
 */
val appContext = module {
    single(named("appContext")) { androidContext() }
}

/**
 * Room Database module.
 */
val roomModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "movie-database"
        ).build()
    }
    single { get<AppDatabase>().favoriteDao() }
    single { get<AppDatabase>().detailsDao() }
}

/**
 * Remote module.
 */
val remoteModule = module {
    single { MovieRepository(get()) }
    single { RemoteAccess() }
}

/**
 * Repository module.
 */
val repositoryModule = module {
    single { Repository(get(), get(), get()) }
    factory { ConnectivityUtils(get()) }
}

/**
 * View model modules
 */
val viewModelModules = module {
    viewModel { MainActivityViewModel(get(), get(), WeakReference(androidApplication())) }
    viewModel { DetailActivityViewModel(get(), get(), WeakReference(androidApplication())) }
}

/**
 * Used to inject dependencies in the module list.
 */
val appComponent: List<Module> = listOf(appContext, roomModule, remoteModule, repositoryModule, viewModelModules)

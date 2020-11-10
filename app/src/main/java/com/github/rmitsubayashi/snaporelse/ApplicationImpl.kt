package com.github.rmitsubayashi.snaporelse

import android.app.Application
import com.github.rmitsubayashi.snaporelse.di.repositoryModules
import com.github.rmitsubayashi.snaporelse.di.useCaseModules
import com.github.rmitsubayashi.snaporelse.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ApplicationImpl: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ApplicationImpl)
            modules(viewModelModules, repositoryModules, useCaseModules)
        }
    }
}
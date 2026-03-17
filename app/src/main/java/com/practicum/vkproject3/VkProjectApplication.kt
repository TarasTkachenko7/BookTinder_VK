package com.practicum.vkproject3

import android.app.Application
import com.practicum.vkproject3.di.dataModule
import com.practicum.vkproject3.di.domainModule
import com.practicum.vkproject3.di.networkModule
import com.practicum.vkproject3.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class VkProjectApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@VkProjectApplication)
            modules(networkModule, dataModule, domainModule, viewModelModule)
        }
    }
}

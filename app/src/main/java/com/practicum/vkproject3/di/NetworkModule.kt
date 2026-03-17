package com.practicum.vkproject3.di

import com.practicum.vkproject3.data.network.RetrofitClient
import org.koin.dsl.module

val networkModule = module {
    single { RetrofitClient.authApi }
    single { RetrofitClient.openLibraryApi }
}

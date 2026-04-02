package com.practicum.vkproject3.di

import com.practicum.vkproject3.data.auth.AuthRepositoryImpl
import com.practicum.vkproject3.data.books.BookRepositoryImpl
import com.practicum.vkproject3.data.profile.UserRepositoryImpl
import com.practicum.vkproject3.domain.auth.AuthRepository
import com.practicum.vkproject3.domain.books.BookRepository
import com.practicum.vkproject3.domain.profile.UserRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import com.practicum.vkproject3.domain.books.GigaChatRepository
import com.practicum.vkproject3.data.books.GigaChatRepositoryImpl

val dataModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<BookRepository> { BookRepositoryImpl(get(), androidContext()) }
    single<UserRepository> { UserRepositoryImpl(androidContext()) }
    single<GigaChatRepository> { GigaChatRepositoryImpl(get()) }
}
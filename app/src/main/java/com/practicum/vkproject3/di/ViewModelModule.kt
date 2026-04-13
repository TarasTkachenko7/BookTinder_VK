package com.practicum.vkproject3.di

import com.practicum.vkproject3.presentation.auth.ForgotPasswordViewModel
import com.practicum.vkproject3.presentation.auth.LoginViewModel
import com.practicum.vkproject3.presentation.auth.RegistrationViewModel
import com.practicum.vkproject3.presentation.auth.VerificationViewModel
import com.practicum.vkproject3.presentation.books.BookViewModel
import com.practicum.vkproject3.presentation.discussions.DiscussionsViewModel
import com.practicum.vkproject3.presentation.genres.GenrePickViewModel
import com.practicum.vkproject3.presentation.home.HomeViewModel
import com.practicum.vkproject3.presentation.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { RegistrationViewModel(get()) }
    viewModel { VerificationViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }

    viewModel { BookViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { GenrePickViewModel(androidContext(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { DiscussionsViewModel(get()) }
}
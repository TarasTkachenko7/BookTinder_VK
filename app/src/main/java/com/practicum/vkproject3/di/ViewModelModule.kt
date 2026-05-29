package com.practicum.vkproject3.di

import com.practicum.vkproject3.presentation.auth.ForgotPasswordViewModel
import com.practicum.vkproject3.presentation.auth.LoginViewModel
import com.practicum.vkproject3.presentation.auth.RegistrationViewModel
import com.practicum.vkproject3.presentation.auth.VerificationViewModel
import com.practicum.vkproject3.presentation.books.BookDetailsViewModel
import com.practicum.vkproject3.presentation.books.BookViewModel
import com.practicum.vkproject3.presentation.books.GenreDetailsViewModel
import com.practicum.vkproject3.presentation.discussions.DiscussionsViewModel
import com.practicum.vkproject3.presentation.genres.GenrePickViewModel
import com.practicum.vkproject3.presentation.home.HomeViewModel
import com.practicum.vkproject3.presentation.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get(), androidContext()) }
    viewModel { RegistrationViewModel(get(), androidContext()) }
    viewModel { VerificationViewModel(get(), androidContext()) }
    viewModel { ForgotPasswordViewModel(get(), androidContext()) }

    viewModel { BookViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), androidContext()) }
    viewModel { GenrePickViewModel(get(), androidContext(), get()) }
    viewModel { ProfileViewModel(get(), get(), androidContext()) }

    viewModel { DiscussionsViewModel(get(), get(), androidContext()) }

    viewModel { com.practicum.vkproject3.presentation.books.CatalogViewModel(get(), androidContext()) }
    viewModel { parameters -> GenreDetailsViewModel(genre = parameters.get(), repository = get(), context = androidContext()) }
    viewModel { BookDetailsViewModel(get()) }
    viewModel { com.practicum.vkproject3.presentation.books.FavoritesViewModel(get()) }
}

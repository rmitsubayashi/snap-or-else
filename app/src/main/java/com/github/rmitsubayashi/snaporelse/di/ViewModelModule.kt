package com.github.rmitsubayashi.snaporelse.di

import com.github.rmitsubayashi.snaporelse.view.addchallenge.AddChallengeViewModel
import com.github.rmitsubayashi.snaporelse.view.challengedetails.ChallengeDetailsViewModel
import com.github.rmitsubayashi.snaporelse.view.challengelist.ChallengeListViewModel
import com.github.rmitsubayashi.snaporelse.view.photodetails.PhotoDetailsViewModel
import com.github.rmitsubayashi.snaporelse.view.photolist.PhotoListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { ChallengeListViewModel(get()) }
    viewModel { AddChallengeViewModel(get(), get(), get()) }
    viewModel { ChallengeDetailsViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { PhotoListViewModel(get()) }
    viewModel { PhotoDetailsViewModel(get(), get(), get(), get()) }
}
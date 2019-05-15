package com.beloushkin.android.learn.countries.di

import com.beloushkin.android.learn.countries.model.CountriesService
import com.beloushkin.android.learn.countries.viewmodel.ListViewModel
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(service: CountriesService)

    fun inject(viewModel: ListViewModel)
}
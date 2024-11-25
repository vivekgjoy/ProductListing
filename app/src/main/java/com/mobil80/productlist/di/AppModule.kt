package com.mobil80.productlist.di

import com.mobil80.productlist.core.Constants
import com.mobil80.productlist.data.network.ApiService
import com.mobil80.productlist.presentation.viewmodels.ProductViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(ApiService::class.java)
    }

    viewModel { ProductViewModel(get()) }
}

package com.xpyx.nokiahslvisualisation.di

import com.xpyx.nokiahslvisualisation.repository.AlertRepository
import com.xpyx.nokiahslvisualisation.repository.AlertRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    @ViewModelScoped
    abstract fun bindRepository(repo: AlertRepositoryImpl): AlertRepository

}
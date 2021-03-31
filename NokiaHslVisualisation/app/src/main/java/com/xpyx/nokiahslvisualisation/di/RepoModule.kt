package com.xpyx.nokiahslvisualisation.di

import com.xpyx.nokiahslvisualisation.networking.apolloClient.AlertsAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Singleton
    @Provides
    fun provideWebService() = AlertsAPI()

}
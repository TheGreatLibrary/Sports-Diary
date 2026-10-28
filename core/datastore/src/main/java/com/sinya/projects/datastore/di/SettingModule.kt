package com.sinya.projects.datastore.di

import com.sinya.projects.datastore.providers.LanguageProviderImpl
import com.sinya.projects.datastore.providers.MapStateProviderImpl
import com.sinya.projects.domain.providers.LanguageProvider
import com.sinya.projects.domain.providers.MapStateProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LanguageModule {

    @Binds
    @Singleton
    abstract fun bindLanguageProvider(
        impl: LanguageProviderImpl
    ): LanguageProvider

    @Binds
    @Singleton
    abstract fun bindMapStateProvider(
        impl: MapStateProviderImpl
    ): MapStateProvider
}

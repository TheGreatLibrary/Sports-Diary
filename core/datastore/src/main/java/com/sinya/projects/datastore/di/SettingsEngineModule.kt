package com.sinya.projects.datastore.di

import com.sinya.projects.datastore.SettingsEngineImpl
import com.sinya.projects.datastore.providers.LanguageProviderImpl
import com.sinya.projects.datastore.providers.MapStateProviderImpl
import com.sinya.projects.datastore.providers.MorningRoutineIdProviderImpl
import com.sinya.projects.domain.managers.SettingsEngine
import com.sinya.projects.domain.providers.LanguageProvider
import com.sinya.projects.domain.providers.MapStateProvider
import com.sinya.projects.domain.providers.MorningRoutineIdProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SettingsEngineModule {

    @Binds
    @Singleton
    abstract fun bindSettingsEngine(
        impl: SettingsEngineImpl
    ): SettingsEngine

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

    @Binds
    @Singleton
    abstract fun bindMorningRoutineIdProvider(
        impl: MorningRoutineIdProviderImpl
    ): MorningRoutineIdProvider
}

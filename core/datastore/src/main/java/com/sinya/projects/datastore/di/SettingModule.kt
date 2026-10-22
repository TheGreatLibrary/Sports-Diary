package com.sinya.projects.datastore.di

import com.sinya.projects.datastore.LanguageProviderImpl
import com.sinya.projects.domain.repository.LanguageProvider
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
}

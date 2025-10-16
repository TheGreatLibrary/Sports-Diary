package com.sinya.projects.sportsdiary.di

import com.sinya.projects.sportsdiary.data.database.repository.ExercisesRepository
import com.sinya.projects.sportsdiary.data.database.repository.ExercisesRepositoryImpl
import com.sinya.projects.sportsdiary.data.database.repository.MorningRepository
import com.sinya.projects.sportsdiary.data.database.repository.MorningRepositoryImpl
import com.sinya.projects.sportsdiary.data.database.repository.ProportionRepository
import com.sinya.projects.sportsdiary.data.database.repository.ProportionRepositoryImpl
import com.sinya.projects.sportsdiary.data.database.repository.TrainingRepository
import com.sinya.projects.sportsdiary.data.database.repository.TrainingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    @Singleton
    abstract fun bindTrainingRepository(
        impl: TrainingRepositoryImpl
    ) : TrainingRepository

    @Binds
    @Singleton
    abstract fun bindExercisesRepository(
        impl: ExercisesRepositoryImpl
    ) : ExercisesRepository

    @Binds
    @Singleton
    abstract fun bindProportionRepository(
        impl: ProportionRepositoryImpl
    ) : ProportionRepository

    @Binds
    @Singleton
    abstract fun bindMorningRepository(
        impl: MorningRepositoryImpl
    ) : MorningRepository
}
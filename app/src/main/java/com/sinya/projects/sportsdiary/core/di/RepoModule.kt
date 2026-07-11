package com.sinya.projects.sportsdiary.core.di

import com.sinya.projects.sportsdiary.core.data.dataBase.BackupManager
import com.sinya.projects.sportsdiary.core.data.dataBase.BackupManagerImpl
import com.sinya.projects.sportsdiary.core.domain.repository.CategoryRepository
import com.sinya.projects.sportsdiary.core.domain.repository.CategoryRepositoryImpl
import com.sinya.projects.sportsdiary.core.domain.repository.ExercisesRepository
import com.sinya.projects.sportsdiary.core.domain.repository.ExercisesRepositoryImpl
import com.sinya.projects.sportsdiary.core.domain.repository.ChangelogRepository
import com.sinya.projects.sportsdiary.core.domain.repository.ChangelogRepositoryImpl
import com.sinya.projects.sportsdiary.core.domain.repository.MorningRepository
import com.sinya.projects.sportsdiary.core.domain.repository.MorningRepositoryImpl
import com.sinya.projects.sportsdiary.core.domain.repository.ProportionRepository
import com.sinya.projects.sportsdiary.core.domain.repository.ProportionRepositoryImpl
import com.sinya.projects.sportsdiary.core.domain.repository.TrainingRepository
import com.sinya.projects.sportsdiary.core.domain.repository.TrainingRepositoryImpl
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
    abstract fun bindChangelogRepository(
        impl: ChangelogRepositoryImpl
    ): ChangelogRepository

    @Binds
    @Singleton
    abstract fun bindBackupManager(
        impl: BackupManagerImpl
    ): BackupManager

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

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ) : CategoryRepository
}
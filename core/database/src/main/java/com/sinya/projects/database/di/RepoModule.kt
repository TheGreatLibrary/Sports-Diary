package com.sinya.projects.database.di

import com.sinya.projects.database.BackupManagerImpl
import com.sinya.projects.database.repoImpl.CategoryRepositoryImpl
import com.sinya.projects.database.repoImpl.ChangelogRepositoryImpl
import com.sinya.projects.database.repoImpl.ExercisesRepositoryImpl
import com.sinya.projects.database.repoImpl.MorningRepositoryImpl
import com.sinya.projects.database.repoImpl.ProportionRepositoryImpl
import com.sinya.projects.database.repoImpl.TrainingRepositoryImpl
import com.sinya.projects.domain.managers.BackupManager
import com.sinya.projects.domain.repository.CategoryRepository
import com.sinya.projects.domain.repository.ChangelogRepository
import com.sinya.projects.domain.repository.ExercisesRepository
import com.sinya.projects.domain.repository.MorningRepository
import com.sinya.projects.domain.repository.ProportionRepository
import com.sinya.projects.domain.repository.TrainingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepoModule {

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
package com.sinya.projects.sportsdiary.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sinya.projects.sportsdiary.data.database.AppDatabase
import com.sinya.projects.sportsdiary.data.database.dao.DataMorningDao
import com.sinya.projects.sportsdiary.data.database.dao.ExercisesDao
import com.sinya.projects.sportsdiary.data.database.dao.PlanMorningDao
import com.sinya.projects.sportsdiary.data.database.dao.ProportionsDao
import com.sinya.projects.sportsdiary.data.database.dao.TrainingsDao
import com.sinya.projects.sportsdiary.data.database.dao.TypeTrainingDao
import com.sinya.projects.sportsdiary.data.database.repository.TrainingRepository
import com.sinya.projects.sportsdiary.data.database.repository.TrainingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        val MIGRATION_1_2 = object : Migration(1, 2) { override fun migrate(db: SupportSQLiteDatabase) {} }

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "diary.db"
        )
        .createFromAsset("diary.db")
        .addMigrations(MIGRATION_1_2)
        .build()


    }



    @Provides
    fun provideTypeTrainingDao(db: AppDatabase): TypeTrainingDao = db.typeTrainingDao()

    @Provides
    fun provideTrainingDao(db: AppDatabase): TrainingsDao = db.trainingsDao()

    @Provides
    fun provideExercisesDao(db: AppDatabase) : ExercisesDao = db.exercisesDao()

    @Provides
    fun provideProportionsDao(db: AppDatabase) : ProportionsDao = db.proportionsDao()

    @Provides
    fun provideDataMorningDao(db: AppDatabase) : DataMorningDao = db.dataMorningDao()

    @Provides
    fun providePlanMorningDao(db: AppDatabase) : PlanMorningDao = db.planMorningDao()
}
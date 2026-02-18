package com.sinya.projects.sportsdiary.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sinya.projects.sportsdiary.data.database.AppDatabase
import com.sinya.projects.sportsdiary.data.database.DatabaseMigrations.MIGRATION_1_2
import com.sinya.projects.sportsdiary.data.database.DatabaseMigrations.MIGRATION_2_3
import com.sinya.projects.sportsdiary.data.database.DatabaseMigrations.MIGRATION_3_4
import com.sinya.projects.sportsdiary.data.database.DatabaseMigrations.MIGRATION_4_5
import com.sinya.projects.sportsdiary.data.database.DatabaseMigrations.MIGRATION_5_6
import com.sinya.projects.sportsdiary.data.database.DatabaseMigrations.MIGRATION_6_7
import com.sinya.projects.sportsdiary.data.database.dao.DataMorningDao
import com.sinya.projects.sportsdiary.data.database.dao.ExercisesDao
import com.sinya.projects.sportsdiary.data.database.dao.PlanMorningDao
import com.sinya.projects.sportsdiary.data.database.dao.ProportionsDao
import com.sinya.projects.sportsdiary.data.database.dao.TrainingsDao
import com.sinya.projects.sportsdiary.data.database.dao.TypeTrainingDao
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


        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "diary.db"
        )
        .createFromAsset("diary.db")
        .addMigrations(MIGRATION_1_2)
        .addMigrations(MIGRATION_2_3)
        .addMigrations(MIGRATION_3_4)
        .addMigrations(MIGRATION_4_5)
        .addMigrations(MIGRATION_5_6)
        .addMigrations(MIGRATION_6_7)
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
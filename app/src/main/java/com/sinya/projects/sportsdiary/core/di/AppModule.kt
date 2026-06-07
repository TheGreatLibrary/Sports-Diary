package com.sinya.projects.sportsdiary.core.di

import android.content.Context
import androidx.room.Room
import com.sinya.projects.sportsdiary.core.data.dataBase.AppDatabase
import com.sinya.projects.sportsdiary.core.data.dataBase.DatabaseMigrations.MIGRATION_1_2
import com.sinya.projects.sportsdiary.core.data.dataBase.DatabaseMigrations.MIGRATION_2_3
import com.sinya.projects.sportsdiary.core.data.dataBase.DatabaseMigrations.MIGRATION_3_4
import com.sinya.projects.sportsdiary.core.data.dataBase.DatabaseMigrations.MIGRATION_4_5
import com.sinya.projects.sportsdiary.core.data.dataBase.DatabaseMigrations.MIGRATION_5_6
import com.sinya.projects.sportsdiary.core.data.dataBase.DatabaseMigrations.MIGRATION_6_7
import com.sinya.projects.sportsdiary.core.data.dataBase.DatabaseMigrations.MIGRATION_7_8
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.DataMorningDao
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.ExercisesDao
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.PlanMorningDao
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.ProportionsDao
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.TrainingsDao
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.TypeTrainingDao
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
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
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
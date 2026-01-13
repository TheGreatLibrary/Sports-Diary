package com.sinya.projects.sportsdiary.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.impl.Migration_1_2
import com.sinya.projects.sportsdiary.data.database.AppDatabase
import com.sinya.projects.sportsdiary.data.database.dao.DataMorningDao
import com.sinya.projects.sportsdiary.data.database.dao.ExercisesDao
import com.sinya.projects.sportsdiary.data.database.dao.PlanMorningDao
import com.sinya.projects.sportsdiary.data.database.dao.ProportionsDao
import com.sinya.projects.sportsdiary.data.database.dao.TrainingsDao
import com.sinya.projects.sportsdiary.data.database.dao.TypeTrainingDao
import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import com.sinya.projects.sportsdiary.domain.repository.TrainingRepositoryImpl
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
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {

                // 1. Добавляем state
                db.execSQL("""
                    ALTER TABLE data_training
                    ADD COLUMN state INTEGER NOT NULL DEFAULT 1
                """.trimIndent())

                // 2. Добавляем order_index
                db.execSQL("""
                    ALTER TABLE data_training
                    ADD COLUMN order_index INTEGER NOT NULL DEFAULT 0
                """.trimIndent())

                db.execSQL("""
                    UPDATE data_training
                    SET order_index = (
                        SELECT COUNT(*)
                        FROM data_training dt2
                        WHERE dt2.training_id = data_training.training_id
                          AND dt2.exercises_id < data_training.exercises_id
                    )
                """.trimIndent())
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
                    ALTER TABLE data_type_trainings
                    ADD COLUMN order_index INTEGER NOT NULL DEFAULT 0
                """.trimIndent())

                db.execSQL("""
                    UPDATE data_type_trainings
                    SET order_index = (
                        SELECT COUNT(*)
                        FROM data_type_trainings dt2
                        WHERE dt2.type_id = data_type_trainings.type_id
                          AND dt2.exercise_id < data_type_trainings.exercise_id
                    )
                """.trimIndent())
            }
        }

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "diary.db"
        )
        .createFromAsset("diary.db")
        .addMigrations(MIGRATION_1_2)
        .addMigrations(MIGRATION_2_3)
        .addMigrations(MIGRATION_3_4)
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
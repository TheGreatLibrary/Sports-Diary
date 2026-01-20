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
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    INSERT OR IGNORE INTO type_training (id, name)
                    VALUES (1, 'not_category')
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE training_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        serial_num INTEGER NOT NULL,
                        type_id INTEGER NOT NULL DEFAULT 1,
                        date TEXT NOT NULL,
                        FOREIGN KEY (type_id) 
                        REFERENCES type_training(id) 
                        ON DELETE SET DEFAULT
                    )
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO training_new (id, serial_num, type_id, date)
                    SELECT 
                        t.id,
                        t.serial_num,
                        CASE 
                            WHEN t.type_id IN (SELECT id FROM type_training) 
                            THEN t.type_id 
                            ELSE 1 
                        END as type_id,
                        t.date
                    FROM trainings t
                """.trimIndent())

                db.execSQL("DROP TABLE trainings")

                db.execSQL("ALTER TABLE training_new RENAME TO trainings")

                db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_type_id ON trainings (type_id)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_date ON trainings (date DESC)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_type_id_date ON trainings (type_id, date)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_date_serial_num ON trainings (date, serial_num)")

                db.execSQL("""
                    UPDATE data_training
                    SET training_id = 1
                    WHERE training_id NOT IN (SELECT id FROM trainings)
                """.trimIndent())

                db.execSQL("""
                    UPDATE data_type_trainings
                    SET type_id = 1
                    WHERE type_id NOT IN (SELECT id FROM type_training)
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
        .addMigrations(MIGRATION_4_5)
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
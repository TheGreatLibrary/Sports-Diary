package com.sinya.projects.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sinya.projects.database.dao.DataMorningDao
import com.sinya.projects.database.dao.ExercisesDao
import com.sinya.projects.database.dao.ProportionsDao
import com.sinya.projects.database.dao.TrainingsDao
import com.sinya.projects.database.dao.TypeTrainingDao
import com.sinya.projects.database.entity.Categories
import com.sinya.projects.database.entity.CategoryTranslation
import com.sinya.projects.database.entity.DataMorning
import com.sinya.projects.database.entity.DataProportions
import com.sinya.projects.database.entity.DataTraining
import com.sinya.projects.database.entity.DataTypeTrainings
import com.sinya.projects.database.entity.EquipmentTranslation
import com.sinya.projects.database.entity.Equipments
import com.sinya.projects.database.entity.ExerciseMuscles
import com.sinya.projects.database.entity.ExerciseTranslations
import com.sinya.projects.database.entity.Exercises
import com.sinya.projects.database.entity.ForceTranslation
import com.sinya.projects.database.entity.Forces
import com.sinya.projects.database.entity.LevelTranslation
import com.sinya.projects.database.entity.Levels
import com.sinya.projects.database.entity.MechanicTranslation
import com.sinya.projects.database.entity.Mechanics
import com.sinya.projects.database.entity.MuscleTranslations
import com.sinya.projects.database.entity.Muscles
import com.sinya.projects.database.entity.Proportions
import com.sinya.projects.database.entity.Trainings
import com.sinya.projects.database.entity.TypeProportionTranslations
import com.sinya.projects.database.entity.TypeProportions
import com.sinya.projects.database.entity.TypeTraining
import com.sinya.projects.database.entity.UnitsMeasurement

@Database(
    entities = [
        Exercises::class,
        ExerciseTranslations::class,
        ExerciseMuscles::class,

        Forces::class,
        ForceTranslation::class,
        Levels::class,
        LevelTranslation::class,
        Mechanics::class,
        MechanicTranslation::class,
        Equipments::class,
        EquipmentTranslation::class,
        Categories::class,
        CategoryTranslation::class,

        Muscles::class,
        MuscleTranslations::class,

        DataMorning::class,

        Trainings::class,
        DataTraining::class,
        TypeTraining::class,
        DataTypeTrainings::class,

        Proportions::class,
        DataProportions::class,
        TypeProportions::class,
        TypeProportionTranslations::class,

        UnitsMeasurement::class
    ],
    version = AppDatabase.DB_VERSION,
    exportSchema = false
)
internal abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DB_VERSION = 11
    }

    abstract fun typeTrainingDao() : TypeTrainingDao
    abstract fun trainingsDao() : TrainingsDao
    abstract fun exercisesDao() : ExercisesDao
    abstract fun proportionsDao() : ProportionsDao
    abstract fun dataMorningDao() : DataMorningDao
}


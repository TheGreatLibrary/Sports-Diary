package com.sinya.projects.sportsdiary.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sinya.projects.sportsdiary.data.database.dao.DataMorningDao
import com.sinya.projects.sportsdiary.data.database.dao.ExercisesDao
import com.sinya.projects.sportsdiary.data.database.dao.PlanMorningDao
import com.sinya.projects.sportsdiary.data.database.dao.ProportionsDao
import com.sinya.projects.sportsdiary.data.database.dao.TrainingsDao
import com.sinya.projects.sportsdiary.data.database.dao.TypeTrainingDao
import com.sinya.projects.sportsdiary.data.database.entity.Categories
import com.sinya.projects.sportsdiary.data.database.entity.CategoryTranslation
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.data.database.entity.DataPlanMorning
import com.sinya.projects.sportsdiary.data.database.entity.DataProportions
import com.sinya.projects.sportsdiary.data.database.entity.DataTraining
import com.sinya.projects.sportsdiary.data.database.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.data.database.entity.EquipmentTranslation
import com.sinya.projects.sportsdiary.data.database.entity.Equipments
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseMuscles
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.data.database.entity.Exercises
import com.sinya.projects.sportsdiary.data.database.entity.ForceTranslation
import com.sinya.projects.sportsdiary.data.database.entity.Forces
import com.sinya.projects.sportsdiary.data.database.entity.LevelTranslation
import com.sinya.projects.sportsdiary.data.database.entity.Levels
import com.sinya.projects.sportsdiary.data.database.entity.MechanicTranslation
import com.sinya.projects.sportsdiary.data.database.entity.Mechanics
import com.sinya.projects.sportsdiary.data.database.entity.MuscleTranslations
import com.sinya.projects.sportsdiary.data.database.entity.Muscles
import com.sinya.projects.sportsdiary.data.database.entity.PlanMornings
import com.sinya.projects.sportsdiary.data.database.entity.Proportions
import com.sinya.projects.sportsdiary.data.database.entity.Trainings
import com.sinya.projects.sportsdiary.data.database.entity.TypeProportionTranslations
import com.sinya.projects.sportsdiary.data.database.entity.TypeProportions
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.data.database.entity.UnitsMeasurement

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
        PlanMornings::class,
        DataPlanMorning::class,

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
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun typeTrainingDao() : TypeTrainingDao
    abstract fun trainingsDao() : TrainingsDao
    abstract fun exercisesDao() : ExercisesDao
    abstract fun proportionsDao() : ProportionsDao
    abstract fun dataMorningDao() : DataMorningDao
    abstract fun planMorningDao() : PlanMorningDao
}


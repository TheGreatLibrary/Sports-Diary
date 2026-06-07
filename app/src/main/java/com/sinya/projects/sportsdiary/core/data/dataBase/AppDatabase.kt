package com.sinya.projects.sportsdiary.core.data.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.DataMorningDao
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.ExercisesDao
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.PlanMorningDao
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.ProportionsDao
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.TrainingsDao
import com.sinya.projects.sportsdiary.core.data.dataBase.dao.TypeTrainingDao
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Categories
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.CategoryTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataMorning
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataPlanMorning
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataProportions
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataTraining
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataTypeTrainings
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.EquipmentTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Equipments
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.ExerciseMuscles
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Exercises
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.ForceTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Forces
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.LevelTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Levels
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.MechanicTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Mechanics
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.MuscleTranslations
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Muscles
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.PlanMornings
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Proportions
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Trainings
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeProportionTranslations
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeProportions
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.TypeTraining
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.UnitsMeasurement

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
    version = 8,
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


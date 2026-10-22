package com.sinya.projects.database.mapper

import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.model.ProportionsEntry
import com.sinya.projects.domain.model.DataTrainingEntry
import com.sinya.projects.domain.model.DataTypeTrainingsEntry
import com.sinya.projects.domain.model.TrainingsEntry
import com.sinya.projects.domain.model.TypeTrainingEntry
import com.sinya.projects.domain.model.ExerciseDescriptionEntry
import com.sinya.projects.domain.model.ExerciseMusclesEntry
import com.sinya.projects.domain.model.ExerciseTranslationsEntry
import com.sinya.projects.domain.model.ExercisesEntry
import com.sinya.projects.database.entity.CategoryTranslation
import com.sinya.projects.database.entity.DataMorning
import com.sinya.projects.database.entity.DataTraining
import com.sinya.projects.database.entity.DataTypeTrainings
import com.sinya.projects.database.entity.EquipmentTranslation
import com.sinya.projects.database.entity.ExerciseMuscles
import com.sinya.projects.database.entity.ExerciseTranslations
import com.sinya.projects.database.entity.Exercises
import com.sinya.projects.database.entity.ForceTranslation
import com.sinya.projects.database.entity.LevelTranslation
import com.sinya.projects.database.entity.MechanicTranslation
import com.sinya.projects.database.entity.Proportions
import com.sinya.projects.database.entity.Trainings
import com.sinya.projects.database.entity.TypeTraining

internal fun DataMorning.toDomain(): MorningEntry {
    return MorningEntry(
        id = this.id,
        note = this.note,
        date = this.date,
        categoryId = this.categoryId
    )
}

internal fun Proportions.toDomain(): ProportionsEntry {
    return ProportionsEntry(
        id = this.id,
        date = this.date
    )
}

internal fun TypeTraining.toDomain(): TypeTrainingEntry {
    return TypeTrainingEntry(
        id = this.id,
        name = this.name
    )
}

internal fun DataTraining.toDomain(): DataTrainingEntry {
    return DataTrainingEntry(
        trainingId = this.trainingId,
        exerciseId = this.exerciseId,
        countResult = this.countResult,
        weightResult = this.weightResult,
        state = this.state,
        mode = this.mode,
        weight = this.weight,
        orderIndex = this.orderIndex
    )
}

internal fun DataTypeTrainings.toDomain(): DataTypeTrainingsEntry {
    return DataTypeTrainingsEntry(
        typeId = this.typeId,
        exerciseId = this.exerciseId,
        mode = this.mode,
        weight = this.weight,
        orderIndex = this.orderIndex
    )
}

internal fun Trainings.toDomain(): TrainingsEntry {
    return TrainingsEntry(
        id = this.id,
        typeId = this.typeId,
        serialNum = this.serialNum,
        date = this.date
    )
}

internal fun Exercises.toDomain(): ExercisesEntry {
    return ExercisesEntry(
        id = this.id,
        icon = this.icon,
        isCustom = this.isCustom,
        forceId = this.forceId,
        levelId = this.levelId,
        mechanicId = this.mechanicId,
        equipmentId = this.equipmentId,
        categoryId = this.categoryId
    )
}

internal fun ExerciseTranslations.toDomain(): ExerciseTranslationsEntry {
    return ExerciseTranslationsEntry(
        id = this.exerciseId,
        language = this.language,
        name = this.name,
        description = this.description,
        rule = this.rule
    )
}

internal fun ExerciseMuscles.toDomain(): ExerciseMusclesEntry {
    return ExerciseMusclesEntry(
        muscleId = this.muscleId,
        exerciseId = this.exerciseId,
        value = this.value
    )
}

internal fun ForceTranslation.toDomain(): ExerciseDescriptionEntry {
    return ExerciseDescriptionEntry(
        id = this.forceId,
        language = this.language,
        name = this.name
    )
}


internal fun EquipmentTranslation.toDomain(): ExerciseDescriptionEntry {
    return ExerciseDescriptionEntry(
        id = this.eqId,
        language = this.language,
        name = this.name
    )
}


internal fun MechanicTranslation.toDomain(): ExerciseDescriptionEntry {
    return ExerciseDescriptionEntry(
        id = this.mechanicId,
        language = this.language,
        name = this.name
    )
}


internal fun LevelTranslation.toDomain(): ExerciseDescriptionEntry {
    return ExerciseDescriptionEntry(
        id = this.levelId,
        language = this.language,
        name = this.name
    )
}


internal fun CategoryTranslation.toDomain(): ExerciseDescriptionEntry {
    return ExerciseDescriptionEntry(
        id = this.categoryId,
        language = this.language,
        name = this.name
    )
}


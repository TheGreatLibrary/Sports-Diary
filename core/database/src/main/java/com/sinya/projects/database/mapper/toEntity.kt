package com.sinya.projects.database.mapper

import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.model.ProportionsEntry
import com.sinya.projects.domain.model.DataTrainingEntry
import com.sinya.projects.domain.model.DataTypeTrainingsEntry
import com.sinya.projects.domain.model.TrainingsEntry
import com.sinya.projects.domain.model.TypeTrainingEntry
import com.sinya.projects.domain.model.ExerciseDescriptionEntry
import com.sinya.projects.domain.model.ExerciseTranslationsEntry
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
import com.sinya.projects.domain.model.ExerciseMusclesEntry
import com.sinya.projects.domain.model.ExercisesEntry

internal fun MorningEntry.toEntity(): DataMorning {
    return DataMorning(
        id = this.id,
        note = this.note,
        date = this.date,
        categoryId = this.categoryId
    )
}

internal fun ProportionsEntry.toEntity(): Proportions {
    return Proportions(
        id = this.id,
        date = this.date
    )
}

internal fun TypeTrainingEntry.toEntity() : TypeTraining {
    return TypeTraining(
        id = this.id ?: 0,
        name = this.name
    )
}

internal fun DataTrainingEntry.toEntity(): DataTraining {
    return DataTraining(
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

internal fun DataTypeTrainingsEntry.toEntity(): DataTypeTrainings {
    return DataTypeTrainings(
        typeId = this.typeId,
        exerciseId = this.exerciseId,
        mode = this.mode,
        weight = this.weight,
        orderIndex = this.orderIndex
    )
}


internal fun TrainingsEntry.toEntity(): Trainings {
    return Trainings(
        id = this.id,
        typeId = this.typeId,
        serialNum = this.serialNum,
        date = this.date
    )
}

internal fun ExercisesEntry.toEntity(): Exercises {
    return Exercises(
        id = this.id,
        icon = this.icon,
        gifUrl = this.gifUrl,
        isCustom = this.isCustom,
        forceId = this.forceId,
        levelId = this.levelId,
        mechanicId = this.mechanicId,
        equipmentId = this.equipmentId,
        categoryId = this.categoryId
    )
}

internal fun ExerciseTranslationsEntry.toEntity(): ExerciseTranslations {
    return ExerciseTranslations(
        exerciseId = this.id,
        language = this.language,
        name = this.name,
        description = this.description,
        rule = this.rule
    )
}

internal fun ExerciseMusclesEntry.toEntity(): ExerciseMuscles {
    return ExerciseMuscles(
        muscleId = this.muscleId,
        exerciseId = this.exerciseId,
        value = this.value
    )
}

internal fun ExerciseDescriptionEntry.toForceEntity(): ForceTranslation {
    return ForceTranslation(
        forceId = this.id,
        language = this.language,
        name = this.name
    )
}


internal fun ExerciseDescriptionEntry.toEquipmentEntity(): EquipmentTranslation {
    return EquipmentTranslation(
        eqId = this.id,
        language = this.language,
        name = this.name
    )
}


internal fun ExerciseDescriptionEntry.toMechanicEntity(): MechanicTranslation {
    return MechanicTranslation(
        mechanicId = this.id,
        language = this.language,
        name = this.name
    )
}


internal fun ExerciseDescriptionEntry.toLevelEntity(): LevelTranslation {
    return LevelTranslation(
        levelId = this.id,
        language = this.language,
        name = this.name
    )
}


internal fun ExerciseDescriptionEntry.toCategoryEntity(): CategoryTranslation {
    return CategoryTranslation(
        categoryId = this.id,
        language = this.language,
        name = this.name
    )
}


package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.data.database.entity.ExerciseMuscles
import com.sinya.projects.sportsdiary.data.database.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.data.database.entity.Exercises
import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class UpsertCustomExerciseUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(ex: Exercises, exData: ExerciseTranslations, exMuscle: List<ExerciseMuscles>): Result<Int> {
        return exerciseRepo.insertNewExercise(ex, exData, exMuscle)
    }
}
package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ExerciseMusclesEntry
import com.sinya.projects.domain.model.ExerciseTranslationsEntry
import com.sinya.projects.domain.model.ExercisesEntry
import com.sinya.projects.domain.repository.ExercisesRepository
import javax.inject.Inject

class UpsertCustomExerciseUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(
        ex: ExercisesEntry,
        exData: ExerciseTranslationsEntry,
        exMuscle: List<ExerciseMusclesEntry>
    ): Result<Int> {
        return exerciseRepo.insertNewExercise(ex, exData, exMuscle)
    }
}
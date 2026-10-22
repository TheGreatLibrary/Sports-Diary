package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.repository.ExercisesRepository
import javax.inject.Inject

class GetExerciseMusclesByIdCategory @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) {
    operator fun invoke(id: Int) = exercisesRepository.getExerciseMusclesByIds(id)
}

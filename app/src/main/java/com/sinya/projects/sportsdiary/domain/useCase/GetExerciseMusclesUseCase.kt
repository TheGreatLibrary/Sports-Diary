package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.domain.model.ExerciseMusclesData
import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetExerciseMusclesUseCase @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) {
    operator fun invoke(id: Int): Flow<List<ExerciseMusclesData>> {
        return exercisesRepository.getExerciseMusclesById(id)
    }
}
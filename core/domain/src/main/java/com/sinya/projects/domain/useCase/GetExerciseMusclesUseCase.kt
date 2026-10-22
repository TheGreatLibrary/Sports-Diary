package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ExerciseMusclesData
import com.sinya.projects.domain.repository.ExercisesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExerciseMusclesUseCase @Inject constructor(
    private val exercisesRepository: ExercisesRepository
) {
    operator fun invoke(id: Int): Flow<List<ExerciseMusclesData>> {
        return exercisesRepository.getExerciseMusclesById(id)
    }
}
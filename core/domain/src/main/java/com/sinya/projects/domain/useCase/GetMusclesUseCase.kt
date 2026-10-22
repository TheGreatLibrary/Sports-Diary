package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ExerciseMusclesData
import com.sinya.projects.domain.repository.ExercisesRepository
import javax.inject.Inject

class GetMusclesUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(): Result<List<ExerciseMusclesData>> {
        return exerciseRepo.getMuscles()
    }
}
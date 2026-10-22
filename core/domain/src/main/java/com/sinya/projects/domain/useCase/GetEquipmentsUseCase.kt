package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ExerciseDescriptionEntry
import com.sinya.projects.domain.repository.ExercisesRepository
import javax.inject.Inject

class GetEquipmentsUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(): Result<List<ExerciseDescriptionEntry>> {
        return exerciseRepo.getEquipments()
    }
}
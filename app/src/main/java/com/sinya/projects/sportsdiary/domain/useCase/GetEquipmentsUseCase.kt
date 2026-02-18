package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.data.database.entity.EquipmentTranslation
import com.sinya.projects.sportsdiary.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class GetEquipmentsUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(): Result<List<EquipmentTranslation>> {
        return exerciseRepo.getEquipments()
    }
}
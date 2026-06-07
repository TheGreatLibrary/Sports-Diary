package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.EquipmentTranslation
import com.sinya.projects.sportsdiary.core.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class GetEquipmentsUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(): Result<List<EquipmentTranslation>> {
        return exerciseRepo.getEquipments()
    }
}
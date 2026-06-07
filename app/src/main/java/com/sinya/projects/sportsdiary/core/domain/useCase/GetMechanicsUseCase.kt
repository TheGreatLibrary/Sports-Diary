package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.MechanicTranslation
import com.sinya.projects.sportsdiary.core.domain.repository.ExercisesRepository
import jakarta.inject.Inject

class GetMechanicsUseCase @Inject constructor(
    private val exerciseRepo: ExercisesRepository
) {
    suspend operator fun invoke(): Result<List<MechanicTranslation>> {
        return exerciseRepo.getMechanics()
    }
}
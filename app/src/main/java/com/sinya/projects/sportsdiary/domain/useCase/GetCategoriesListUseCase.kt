package com.sinya.projects.sportsdiary.domain.useCase

import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetCategoriesListUseCase @Inject constructor(
    private val repoTraining: TrainingRepository
) {
    operator fun invoke(): Flow<List<TypeTraining>> {
        return repoTraining.getCategoriesList()
    }
}
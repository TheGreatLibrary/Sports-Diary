package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.TypeTrainingEntry
import com.sinya.projects.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCategoriesListUseCase @Inject constructor(
    private val repoTraining: TrainingRepository
) {
    operator fun invoke(): Flow<List<TypeTrainingEntry>> {
        return repoTraining.observeCategoriesList()
    }
}
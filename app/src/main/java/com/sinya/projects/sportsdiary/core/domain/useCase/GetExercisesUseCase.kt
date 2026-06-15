package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.repository.ExercisesRepository
import com.sinya.projects.sportsdiary.ui.features.DropdownCheckboxItem
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val repository: ExercisesRepository
) {
    suspend operator fun invoke() : Result<List<DropdownCheckboxItem>> {
        return repository.getExercises()
    }
}
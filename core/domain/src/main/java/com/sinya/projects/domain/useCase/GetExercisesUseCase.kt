package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.DropdownCheckboxItem
import com.sinya.projects.domain.repository.ExercisesRepository
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val repository: ExercisesRepository
) {
    suspend operator fun invoke() : Result<List<DropdownCheckboxItem>> {
        return repository.getExercises()
    }
}
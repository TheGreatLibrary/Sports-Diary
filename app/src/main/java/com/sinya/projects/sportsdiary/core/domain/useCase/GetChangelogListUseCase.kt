package com.sinya.projects.sportsdiary.core.domain.useCase

import com.sinya.projects.sportsdiary.core.domain.model.ChangelogResponse
import com.sinya.projects.sportsdiary.core.domain.repository.ChangelogRepository
import javax.inject.Inject

class GetChangelogListUseCase @Inject constructor(
    private val repository: ChangelogRepository
) {
    suspend operator fun invoke(): Result<ChangelogResponse> {
        return repository.getChangelog()
    }
}
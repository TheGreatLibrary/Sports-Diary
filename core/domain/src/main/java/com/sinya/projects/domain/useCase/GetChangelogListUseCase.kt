package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.ChangelogResponse
import com.sinya.projects.domain.repository.ChangelogRepository
import javax.inject.Inject

class GetChangelogListUseCase @Inject constructor(
    private val repository: ChangelogRepository
) {
    suspend operator fun invoke(): Result<ChangelogResponse> {
        return repository.getChangelog()
    }
}
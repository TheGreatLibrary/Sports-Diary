package com.sinya.projects.domain.repository

import com.sinya.projects.domain.model.ChangelogResponse

interface ChangelogRepository {
    suspend fun getChangelog(): Result<ChangelogResponse>
}

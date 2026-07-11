package com.sinya.projects.sportsdiary.core.domain.repository

import com.sinya.projects.sportsdiary.core.domain.model.ChangelogResponse
import com.sinya.projects.sportsdiary.core.utils.UrlConfig.config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URL
import javax.inject.Inject

interface ChangelogRepository {
    suspend fun getChangelog(): Result<ChangelogResponse>
}

class ChangelogRepositoryImpl @Inject constructor(
    private val json: Json
) : ChangelogRepository {
    override suspend fun getChangelog(): Result<ChangelogResponse> = withContext(Dispatchers.IO) {
        try {
            val response = URL(config).readText()
            Result.success(json.decodeFromString<ChangelogResponse>(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}




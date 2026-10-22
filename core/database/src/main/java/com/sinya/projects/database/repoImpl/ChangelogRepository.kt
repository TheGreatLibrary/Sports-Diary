package com.sinya.projects.database.repoImpl

import com.sinya.projects.domain.model.ChangelogResponse
import com.sinya.projects.domain.repository.ChangelogRepository
import com.sinya.projects.domain.utils.UrlConfig.config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URL
import javax.inject.Inject

internal class ChangelogRepositoryImpl @Inject constructor(
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




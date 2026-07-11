package com.sinya.projects.sportsdiary.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupInfo(
    val backupVersion: Int = 1,
    val appVersion: String,
    val dbVersion: Int,
    val created: String
)
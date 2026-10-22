package com.sinya.projects.domain.repository

import android.net.Uri

interface BackupManager {
    suspend fun export(uri: Uri): Result<Unit>
    suspend fun import(uri: Uri): Result<Unit>
}


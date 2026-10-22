package com.sinya.projects.backup

import android.content.Context
import android.net.Uri

sealed interface BackupEvent {
    data class Import(val uri: Uri) : BackupEvent
    data class Export(val uri: Uri) : BackupEvent
    data class ExportForSharing(val context: Context) : BackupEvent
    object Cancel : BackupEvent
}
package com.sinya.projects.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.repository.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

@HiltViewModel
internal class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager
) : ViewModel() {

    private val _showRestartDialog = MutableStateFlow(false)
    val showRestartDialog = _showRestartDialog.asStateFlow()

    fun onEvent(event: BackupEvent) {
        when (event) {
            is BackupEvent.Import -> import(event.uri)
            is BackupEvent.Export -> export(event.uri)
            is BackupEvent.ExportForSharing -> createBackupForSharing(event.context)
            BackupEvent.Cancel -> _showRestartDialog.value = false
        }
    }

    private fun import(uri: Uri) {
        viewModelScope.launch {
            backupManager.import(uri).onSuccess {
                _showRestartDialog.value = true
            }.onFailure { exception ->
                Log.e("BACKUP_DEBUG", "Ошибка импорта: ${exception.message}", exception)
            }
        }
    }


    private fun export(uri: Uri) {
        viewModelScope.launch {
            backupManager.export(uri).onSuccess {
                _showRestartDialog.value = true
            }.onFailure { exception ->
                Log.e("BACKUP_DEBUG", "Ошибка экспорта: ${exception.message}", exception)
            }
        }
    }

    private fun createBackupForSharing(context: Context) {
        viewModelScope.launch {
            val backupDir = File(context.cacheDir, "backups").apply { mkdirs() }
            val backupFile = File(backupDir, "TrainingDiaryBackup.zip")

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                backupFile
            )

            backupManager.export(uri).onSuccess {

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/zip"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                _showRestartDialog.value = true

                context.startActivity(Intent.createChooser(shareIntent, "Отправить резервную копию"))
            }
        }
    }
}
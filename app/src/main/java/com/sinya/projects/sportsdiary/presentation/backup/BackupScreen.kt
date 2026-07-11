package com.sinya.projects.sportsdiary.presentation.backup

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.model.TypeTopNavigation
import com.sinya.projects.sportsdiary.ui.features.BlockOfCards
import com.sinya.projects.sportsdiary.ui.features.CardWithArrow
import com.sinya.projects.sportsdiary.ui.features.ScreenColumn

@Composable
fun BackupScreen(
    onBackClick: () -> Unit,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val showRestartDialog by viewModel.showRestartDialog.collectAsStateWithLifecycle()

    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) viewModel.onEvent(BackupEvent.Import(uri))
        }
    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
            if (uri != null) viewModel.onEvent(BackupEvent.Export(uri))
        }

    ScreenColumn(
        navigationType = TypeTopNavigation.WithoutIcon(
            onBackClick = onBackClick,
            title = stringResource(R.string.backup_title)
        )
    ) {
        BlockOfCards(title = stringResource(R.string.main_block)) {
            CardWithArrow(
                title = stringResource(R.string.import_db),
                icon = painterResource(R.drawable.back_import),
                onClick = {
                    importLauncher.launch(arrayOf("application/zip"))
                }
            )
            CardWithArrow(
                title = stringResource(R.string.export),
                icon = painterResource(R.drawable.back_export),
                onClick = {
                    exportLauncher.launch("TrainingDiaryBackup.zip")
                }
            )
            CardWithArrow(
                title = stringResource(R.string.share),
                icon = painterResource(R.drawable.back_share),
                onClick = {
                    viewModel.onEvent(BackupEvent.ExportForSharing(context))
                }
            )
        }
    }

    if (showRestartDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.small,
            onDismissRequest = {},
            title = {
                Text(
                    text = stringResource(R.string.complete),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.complete_backup),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val intent =
                            context.packageManager.getLaunchIntentForPackage(context.packageName)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

                        if (intent != null) {
                            context.startActivity(intent)
                        }

                        Runtime.getRuntime().exit(0)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.ok),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        )
    }
}
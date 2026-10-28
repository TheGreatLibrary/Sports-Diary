package com.sinya.projects.database

import android.content.Context
import android.net.Uri
import com.sinya.projects.domain.model.BackupInfo
import com.sinya.projects.domain.managers.BackupManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.Instant
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BackupManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val json: Json
) : BackupManager {

    override suspend fun export(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            // Принудительно записываем данные из WAL в основную БД перед экспортом
            database.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").moveToFirst()
            database.close()

            val dbFile = context.getDatabasePath("diary.db")
            val walFile = context.getDatabasePath("diary.db-wal")
            val shmFile = context.getDatabasePath("diary.db-shm")

            require(dbFile.exists()) { "База данных не найдена." }

            context.contentResolver.openOutputStream(uri)?.use { output ->
                ZipOutputStream(BufferedOutputStream(output)).use { zip ->
                    writeInfo(zip)
                    writeDatabase(zip, dbFile, "diary.db")

                    // Копируем WAL файлы, если они существуют
                    if (walFile.exists()) writeDatabase(zip, walFile, "diary.db-wal")
                    if (shmFile.exists()) writeDatabase(zip, shmFile, "diary.db-shm")
                }
            } ?: error("Не удалось открыть OutputStream.")
        }
    }

    override suspend fun import(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            database.close()

            val dbFile = context.getDatabasePath("diary.db")
            val walFile = context.getDatabasePath("diary.db-wal")
            val shmFile = context.getDatabasePath("diary.db-shm")

            // Удаляем старые WAL файлы, чтобы избежать конфликтов версий после распаковки
            if (walFile.exists()) walFile.delete()
            if (shmFile.exists()) shmFile.delete()

            context.contentResolver.openInputStream(uri)?.use { input ->
                ZipInputStream(BufferedInputStream(input)).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        when (entry.name) {
                            "diary.db" -> dbFile.outputStream().use { zip.copyTo(it) }
                            "diary.db-wal" -> walFile.outputStream().use { zip.copyTo(it) }
                            "diary.db-shm" -> shmFile.outputStream().use { zip.copyTo(it) }
                            "info.json" -> {
                                val outputStream = ByteArrayOutputStream()
                                zip.copyTo(outputStream)
                                val jsonString = outputStream.toString("UTF-8")

                                // 2. Декодируем строку в наш объект BackupInfo
                                val info = json.decodeFromString<BackupInfo>(jsonString)

                                // 3. Проверяем версии перед тем, как перезаписать БД
                                validateBackup(info)
                            }
                        }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            } ?: error("Не удалось открыть InputStream.")
        }
    }

    private fun validateBackup(info: BackupInfo) {
        // Если версия БД в бэкапе выше, чем текущая в приложении — прерываем импорт
        if (info.dbVersion > AppDatabase.DB_VERSION) {
            throw IllegalStateException(
                "Не удалось импортировать. Этот бэкап сделан в более новой версии приложения (БД v${info.dbVersion}). Пожалуйста, обновите приложение."
            )
        }

        // Здесь же можно проверить info.backupVersion, если в будущем изменится структура самого ZIP
    }

    private fun writeDatabase(zip: ZipOutputStream, file: File, entryName: String) {
        zip.putNextEntry(ZipEntry(entryName))
        file.inputStream().use { it.copyTo(zip) }
        zip.closeEntry()
    }

    private fun writeInfo(zip: ZipOutputStream) {
        val info = BackupInfo(
            appVersion = "1",
            dbVersion = AppDatabase.DB_VERSION,
            created = Instant.now().toString()
        )
        zip.putNextEntry(ZipEntry("info.json"))
        zip.write(json.encodeToString(info).toByteArray())
        zip.closeEntry()
    }
}
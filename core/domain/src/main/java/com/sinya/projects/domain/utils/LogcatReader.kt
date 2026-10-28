package com.sinya.projects.domain.utils

import android.content.Context
import android.os.Process
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object LogcatReader {
    suspend fun getAppLogs(context: Context): String = withContext(Dispatchers.IO) {
        val logBuilder = StringBuilder()
        try {
            val packageName = context.packageName
            val myPid = Process.myPid().toString()

            // Считываем логи уровня Error (чтобы не забивать память) со всех сессий
            val process = Runtime.getRuntime().exec("logcat -d -v time *:E")
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

            var isOurLogBlock = false

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue

                val belongsToApp = currentLine.contains(packageName) || currentLine.contains(myPid)

                // Если строка начинается с цифры (дата из флага -v time) или системного тега, это начало новой записи
                val isNewLogEntry = currentLine.firstOrNull()?.isDigit() == true ||
                        currentLine.contains("FATAL EXCEPTION") ||
                        currentLine.contains("AndroidRuntime")

                if (isNewLogEntry) {
                    // Обновляем состояние: если новая запись наша, начинаем собирать блок
                    isOurLogBlock = belongsToApp
                }

                // Записываем строку, если она содержит наш пакет/PID, либо если это продолжение стектрейса нашего приложения
                if (isOurLogBlock || belongsToApp) {
                    logBuilder.append(currentLine).append("\n")
                }
            }
        } catch (e: Exception) {
            logBuilder.append("Ошибка чтения Logcat: ${e.message}")
        }

        if (logBuilder.isEmpty()) "Логи пусты." else logBuilder.toString()
    }
}
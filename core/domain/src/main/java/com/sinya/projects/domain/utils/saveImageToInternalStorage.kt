package com.sinya.projects.domain.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null

        val fileName = "exercise_img_${UUID.randomUUID()}.jpg"

        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream.copyTo(outputStream)

        inputStream.close()
        outputStream.close()

        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
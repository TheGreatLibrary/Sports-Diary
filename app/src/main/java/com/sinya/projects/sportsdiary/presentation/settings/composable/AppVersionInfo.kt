package com.sinya.projects.sportsdiary.presentation.settings.composable

import android.os.Build
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.sinya.projects.sportsdiary.R


@Composable
fun AppVersionInfo(lang: String? = null) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val locale = lang
    val versionName = packageInfo.versionName
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode
    }
    Log.d("AppVersionInfo", "Recomposed with locale: $locale")
    Text(
        stringResource(R.string.version, versionName?:0, versionCode),
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimary,
        style = MaterialTheme.typography.bodyMedium
    )
}
package com.sinya.projects.domain.managers

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sinya.projects.domain.model.HealthData

interface HealthManager {
    val permissions: Set<String>
    suspend fun fetchHealthData(): HealthData
    suspend fun getSdkStatus(): HealthSdkStatus
    fun getPermissionIntent(): Intent
    fun getPermissionContract(): ActivityResultContract<Set<String>, Set<String>>
}

enum class HealthSdkStatus {
    AVAILABLE,
    NOT_INSTALLED,
    NOT_SUPPORTED
}

package com.sinya.projects.health

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.sinya.projects.domain.model.HealthData
import com.sinya.projects.domain.repository.HealthManager
import com.sinya.projects.domain.repository.HealthSdkStatus
import javax.inject.Inject

/**
 * Stub implementation for Huawei Health.
 * To be implemented when HMS Health Kit integration is needed.
 */
internal class HuaweiHealthManagerImpl @Inject constructor() : HealthManager {
    override val permissions: Set<String> = emptySet()

    override suspend fun fetchHealthData(): HealthData = HealthData()

    override suspend fun getSdkStatus(): HealthSdkStatus = HealthSdkStatus.NOT_SUPPORTED

    override fun getPermissionIntent(): Intent = Intent()

    override fun getPermissionContract(): ActivityResultContract<Set<String>, Set<String>> {
        return object : ActivityResultContract<Set<String>, Set<String>>() {
            override fun createIntent(context: android.content.Context, input: Set<String>): Intent = Intent()
            override fun parseResult(resultCode: Int, intent: Intent?): Set<String> = emptySet()
        }
    }
}

package com.sinya.projects.health.di

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import com.sinya.projects.domain.repository.HealthManager
import com.sinya.projects.health.HealthConnectManagerImpl
import com.sinya.projects.health.HuaweiHealthManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object HealthModule {

    @Provides
    @Singleton
    fun provideHealthConnectClient(@ApplicationContext context: Context): HealthConnectClient {
        return HealthConnectClient.getOrCreate(context)
    }

    @Provides
    @Singleton
    fun provideHealthManager(
        @ApplicationContext context: Context,
        healthConnectImpl: HealthConnectManagerImpl,
        huaweiImpl: HuaweiHealthManagerImpl
    ): HealthManager {
        val status = HealthConnectClient.getSdkStatus(context)
        return if (status == HealthConnectClient.SDK_AVAILABLE) {
            healthConnectImpl
        } else {
            huaweiImpl
        }
    }
}

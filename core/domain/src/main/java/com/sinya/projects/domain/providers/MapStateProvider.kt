package com.sinya.projects.domain.providers

import kotlinx.coroutines.flow.Flow

interface MapStateProvider {
    val currentMapState: Flow<Boolean>
}
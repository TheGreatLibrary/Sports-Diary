package com.sinya.projects.domain.providers

import kotlinx.coroutines.flow.Flow

interface MorningRoutineIdProvider {
    val morningRoutineId: Flow<Int?>
}
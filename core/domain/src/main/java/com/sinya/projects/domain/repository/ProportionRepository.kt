package com.sinya.projects.domain.repository

import com.sinya.projects.domain.model.ChartLine
import com.sinya.projects.domain.model.DropdownCheckboxItem
import com.sinya.projects.domain.model.ProportionDialogContent
import com.sinya.projects.domain.model.ProportionItem
import com.sinya.projects.domain.model.ProportionsEntry
import kotlinx.coroutines.flow.Flow

interface ProportionRepository {
    // Proportions
    fun getProportionList(): Flow<List<ProportionsEntry>>
    suspend fun deleteProportion(it: List<ProportionsEntry>): Result<Int>

    // ProportionPage

    suspend fun insertOrUpdateProportion(entity: ProportionItem): Result<Int>
    suspend fun getMeasurementData(list: List<Int>): Result<List<ChartLine>>

    fun observeProportionEntityById(id: Int): Flow<ProportionItem>
    fun observeNewProportion(): Flow<ProportionItem>
    suspend fun getMeasurementDataById(id: Int): Result<ProportionDialogContent>
    suspend fun getMeasurementsList(): Result<List<DropdownCheckboxItem>>
    fun getList(start: String, end: String): Flow<List<ProportionsEntry>>
}



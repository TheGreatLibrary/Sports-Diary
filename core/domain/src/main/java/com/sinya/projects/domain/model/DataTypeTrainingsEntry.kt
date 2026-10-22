package com.sinya.projects.domain.model

data class DataTypeTrainingsEntry(
    val typeId: Int,
    val exerciseId: Int,
    val mode: Int = 0,
    val weight: Int = 0,
    val orderIndex: Int = 0
)
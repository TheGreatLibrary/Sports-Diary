package com.sinya.projects.domain.model

data class TrainingsEntry(
    val id: Int = 0,
    val typeId: Int?,
    val serialNum: Int,
    val date: String
)
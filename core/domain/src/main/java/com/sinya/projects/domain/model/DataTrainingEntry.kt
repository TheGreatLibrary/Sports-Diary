package com.sinya.projects.domain.model

data class DataTrainingEntry(
    val trainingId: Int,
    val exerciseId: Int,
    val countResult: String = "0/0/0/0",
    val weightResult: String = "0/0/0/0",
    val state: Int = 1,
    val mode: Int = 0,
    val weight: Int = 0,
    val orderIndex: Int = 0
)
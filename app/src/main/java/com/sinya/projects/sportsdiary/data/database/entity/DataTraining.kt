package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "data_training",
    primaryKeys = ["training_id", "exercises_id"],
    foreignKeys = [
        ForeignKey(
            entity = Trainings::class,
            parentColumns = ["id"],
            childColumns = ["training_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercises::class,
            parentColumns = ["id"],
            childColumns = ["exercises_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["training_id"]),
        Index(value = ["exercises_id"])
    ]
)
data class DataTraining(
    @ColumnInfo(name = "training_id") val trainingId: Int,
    @ColumnInfo(name = "exercises_id") val exerciseId: Int,
    @ColumnInfo(name = "count_result", defaultValue = "0/0/0/0") val countResult: String = "0/0/0/0",
    @ColumnInfo(name = "weight_result", defaultValue = "0/0/0/0") val weightResult: String = "0/0/0/0",
    @ColumnInfo(name = "state", defaultValue = "1") val state: Int = 1,
    @ColumnInfo(name = "order_index", defaultValue = "0") val orderIndex: Int = 0
)

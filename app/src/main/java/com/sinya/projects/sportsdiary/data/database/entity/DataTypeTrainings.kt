package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "data_type_trainings",
    primaryKeys = ["type_id", "exercise_id"],
    foreignKeys = [
        ForeignKey(
            entity = TypeTraining::class,
            parentColumns = ["id"],
            childColumns = ["type_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercises::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["type_id"]),
        Index(value = ["exercise_id"])
    ]
)
data class DataTypeTrainings(
    @ColumnInfo(name = "type_id") val typeId: Int,
    @ColumnInfo(name = "exercise_id") val exerciseId: Int
)
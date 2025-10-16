package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "training_type_exercise",
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
    ]
)
data class DataTypeTrainings(
    @ColumnInfo(name = "type_id") val typeId: Int,
    @ColumnInfo(name = "exercise_id") val exerciseId: Int
)
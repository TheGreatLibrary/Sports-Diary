package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "exercise_muscles",
    primaryKeys = ["muscle_id", "exercise_id"],
    foreignKeys = [
        ForeignKey(
            entity = Muscles::class,
            parentColumns = ["id"],
            childColumns = ["muscle_id"],
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
data class ExerciseMuscles(
    @ColumnInfo(name = "muscle_id") val muscleId: Int,
    @ColumnInfo(name = "exercise_id") val exerciseId: Int,
    @ColumnInfo(name = "value") val value: Int
)

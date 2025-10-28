package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "data_plan_morning",
    primaryKeys = ["plan_id", "exercise_id"],
    foreignKeys = [
        ForeignKey(
            entity = PlanMornings::class,
            parentColumns = ["id"],
            childColumns = ["plan_id"],
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
        Index(value = ["plan_id"]),
        Index(value = ["exercise_id"])
    ]
)
data class DataPlanMorning(
    @ColumnInfo(name = "plan_id") val planId: Int,
    @ColumnInfo(name = "exercise_id") val exerciseId: Int,
    @ColumnInfo(name = "count_result", defaultValue = "0/0/0/0") val countResult: String = "0/0/0/0",
    @ColumnInfo(name = "weight_result", defaultValue = "0/0/0/0") val weightResult: String = "0/0/0/0"
)


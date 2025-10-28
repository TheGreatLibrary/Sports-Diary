package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index


@Entity(
    tableName = "exercise_translations",
    primaryKeys = ["exercise_id", "language"],
    foreignKeys = [
        ForeignKey(
            entity = Exercises::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["exercise_id"]),
        Index(value = ["language"]),
        Index(value = ["exercise_id", "language"]),
        Index(value = ["name"])
    ]
)
data class ExerciseTranslations(
    @ColumnInfo(name = "exercise_id") val exerciseId: Int,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "rule") val rule: String,
    @ColumnInfo(name = "force") val force: String?,
    @ColumnInfo(name = "level") val level: String?,
    @ColumnInfo(name = "mechanic") val mechanic: String?,
    @ColumnInfo(name = "equipment") val equipment: String?,
    @ColumnInfo(name = "category") val category: String?

)

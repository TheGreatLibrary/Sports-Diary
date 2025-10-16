package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "muscle_translations",
    primaryKeys = ["muscle_id", "language"],
    foreignKeys = [
        ForeignKey(
            entity = Muscles::class,
            parentColumns = ["id"],
            childColumns = ["muscle_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MuscleTranslations(
    @ColumnInfo(name = "muscle_id") val muscleId: Int,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "name") val name: String
)

package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

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
    ],
    indices = [
        Index(value = ["muscle_id"]),
        Index(value = ["language"]),
        Index(value = ["muscle_id", "language"])
    ]
)
data class MuscleTranslations(
    @ColumnInfo(name = "muscle_id") val muscleId: Int,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "name") val name: String
)

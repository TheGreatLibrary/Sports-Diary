package com.sinya.projects.sportsdiary.core.data.dataBase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "level_translation",
    primaryKeys = ["level_id", "language"],
    foreignKeys = [
        ForeignKey(
            entity = Levels::class,
            parentColumns = ["id"],
            childColumns = ["level_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["level_id"]),
        Index(value = ["language"]),
        Index(value = ["level_id", "language"]),
        Index(value = ["name"])
    ]
)
data class LevelTranslation(
    @ColumnInfo(name = "level_id") val levelId: Int,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "name") val name: String
)
package com.sinya.projects.sportsdiary.core.data.dataBase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "mechanic_translation",
    primaryKeys = ["mechanic_id", "language"],
    foreignKeys = [
        ForeignKey(
            entity = Mechanics::class,
            parentColumns = ["id"],
            childColumns = ["mechanic_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mechanic_id"]),
        Index(value = ["language"]),
        Index(value = ["mechanic_id", "language"]),
        Index(value = ["name"])
    ]
)
data class MechanicTranslation(
    @ColumnInfo(name = "mechanic_id") val mechanicId: Int,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "name") val name: String
)
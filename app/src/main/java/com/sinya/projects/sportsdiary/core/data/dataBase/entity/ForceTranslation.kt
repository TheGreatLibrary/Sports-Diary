package com.sinya.projects.sportsdiary.core.data.dataBase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "force_translation",
    primaryKeys = ["force_id", "language"],
    foreignKeys = [
        ForeignKey(
            entity = Forces::class,
            parentColumns = ["id"],
            childColumns = ["force_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["force_id"]),
        Index(value = ["language"]),
        Index(value = ["force_id", "language"]),
        Index(value = ["name"])
    ]
)
data class ForceTranslation(
    @ColumnInfo(name = "force_id") val forceId: Int,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "name") val name: String
)
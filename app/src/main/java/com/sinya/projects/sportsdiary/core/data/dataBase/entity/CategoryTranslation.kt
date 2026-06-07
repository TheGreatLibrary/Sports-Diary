package com.sinya.projects.sportsdiary.core.data.dataBase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "category_translation",
    primaryKeys = ["category_id", "language"],
    foreignKeys = [
        ForeignKey(
            entity = Categories::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["language"]),
        Index(value = ["category_id", "language"]),
        Index(value = ["name"])
    ]
)
data class CategoryTranslation(
    @ColumnInfo(name = "category_id") val categoryId: Int,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "name") val name: String
)
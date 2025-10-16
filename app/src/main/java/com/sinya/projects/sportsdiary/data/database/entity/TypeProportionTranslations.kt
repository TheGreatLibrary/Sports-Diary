package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "type_proportion_translations",
    primaryKeys = ["type_id", "language"],
    foreignKeys = [
        ForeignKey(
            entity = TypeProportions::class,
            parentColumns = ["id"],
            childColumns = ["type_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TypeProportionTranslations(
    @ColumnInfo(name = "type_id") val typeId: Int,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "rule") val rule: String,
)

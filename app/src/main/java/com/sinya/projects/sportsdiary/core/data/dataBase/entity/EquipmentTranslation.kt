package com.sinya.projects.sportsdiary.core.data.dataBase.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "equipment_translation",
    primaryKeys = ["eq_id", "language"],
    foreignKeys = [
        ForeignKey(
            entity = Equipments::class,
            parentColumns = ["id"],
            childColumns = ["eq_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["eq_id"]),
        Index(value = ["language"]),
        Index(value = ["eq_id", "language"]),
        Index(value = ["name"])
    ]
)
data class EquipmentTranslation(
    @ColumnInfo(name = "eq_id") val eqId: Int,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "name") val name: String
)
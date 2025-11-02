package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "type_proportions",
    foreignKeys = [
        ForeignKey(
            entity = UnitsMeasurement::class,
            parentColumns = ["id"],
            childColumns = ["unit_measure_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["unit_measure_id"])
    ]
)
data class TypeProportions(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "unit_measure_id") val unitMeasureId: Int,
    @ColumnInfo(name = "icon") val icon: String?
)

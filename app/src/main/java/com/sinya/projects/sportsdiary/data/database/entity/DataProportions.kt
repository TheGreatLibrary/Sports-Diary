package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "data_proportions",
    primaryKeys = ["proportion_id", "type_id"],
    foreignKeys = [
        ForeignKey(
            entity = Proportions::class,
            parentColumns = ["id"],
            childColumns = ["proportion_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TypeProportions::class,
            parentColumns = ["id"],
            childColumns = ["type_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["proportion_id"]),
        Index(value = ["type_id"])
    ]
)
data class DataProportions(
    @ColumnInfo(name = "proportion_id") val proportionId: Int,
    @ColumnInfo(name = "type_id") val typeId: Int,
    @ColumnInfo(name = "value", defaultValue = "0") val value: Float = 0f
)

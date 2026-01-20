package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trainings",
    foreignKeys = [
        ForeignKey(
            entity = TypeTraining::class,
            parentColumns = ["id"],
            childColumns = ["type_id"],
            onDelete = ForeignKey.SET_DEFAULT
        )
    ],
    indices = [
        Index(value = ["type_id"]),
        Index(value = ["date"], orders = [Index.Order.DESC]),
        Index(value = ["type_id", "date"]),
        Index(value = ["date", "serial_num"])
    ]
)
data class Trainings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "type_id", defaultValue = "1") val typeId: Int = 1,
    @ColumnInfo(name = "serial_num") val serialNum: Int,
    @ColumnInfo(name = "date") val date: String
)

package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "trainings",
    foreignKeys = [
        ForeignKey(
            entity = TypeTraining::class,
            parentColumns = ["id"],
            childColumns = ["type_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Trainings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "type_id") val typeId: Int,
    @ColumnInfo(name = "serial_num") val serialNum: Int,
    @ColumnInfo(name = "date") val date: String
)

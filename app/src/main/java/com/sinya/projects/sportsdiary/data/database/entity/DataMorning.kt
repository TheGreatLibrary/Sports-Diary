package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "data_mornings",
    foreignKeys = [
        ForeignKey(
            entity = PlanMornings::class,
            parentColumns = ["id"],
            childColumns = ["plan_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class DataMorning(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "note") val note: String?,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "plan_id") val planId: Int
)

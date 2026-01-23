package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "data_mornings",
    foreignKeys = [
        ForeignKey(
            entity = PlanMornings::class,
            parentColumns = ["id"],
            childColumns = ["plan_id"],
            onDelete = ForeignKey.SET_NULL
        ),
    ],
    indices = [
        Index(value = ["plan_id"]),
        Index(value = ["date"], orders = [Index.Order.DESC]),
        Index(value = ["date", "plan_id"])
    ]
)
data class DataMorning(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "note") val note: String?,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "plan_id") val planId: Int?
)
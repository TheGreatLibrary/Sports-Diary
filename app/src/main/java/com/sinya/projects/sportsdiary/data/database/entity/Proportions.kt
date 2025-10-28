package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "proportions",
    indices = [
        Index(value = ["date"], orders = [Index.Order.DESC])
    ]
)
data class Proportions(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "date") val date: String = LocalDate.now().toString()
)

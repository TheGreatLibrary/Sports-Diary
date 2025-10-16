package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("muscles")
data class Muscles(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
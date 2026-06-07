package com.sinya.projects.sportsdiary.core.data.dataBase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("levels")
data class Levels(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
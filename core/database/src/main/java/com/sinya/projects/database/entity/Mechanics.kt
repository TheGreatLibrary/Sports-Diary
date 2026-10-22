package com.sinya.projects.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("mechanics")
data class Mechanics(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("forces")
data class Forces(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)


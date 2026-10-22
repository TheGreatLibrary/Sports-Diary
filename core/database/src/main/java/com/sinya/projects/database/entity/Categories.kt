package com.sinya.projects.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("categories")
data class Categories(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
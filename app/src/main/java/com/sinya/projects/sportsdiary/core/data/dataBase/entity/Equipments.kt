package com.sinya.projects.sportsdiary.core.data.dataBase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("equipments")
data class Equipments(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
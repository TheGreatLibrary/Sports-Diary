package com.sinya.projects.sportsdiary.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = Forces::class,
            parentColumns = ["id"],
            childColumns = ["force_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Levels::class,
            parentColumns = ["id"],
            childColumns = ["level_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Mechanics::class,
            parentColumns = ["id"],
            childColumns = ["mechanic_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Equipments::class,
            parentColumns = ["id"],
            childColumns = ["equipment_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Categories::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["force_id"]),
        Index(value = ["level_id"]),
        Index(value = ["mechanic_id"]),
        Index(value = ["equipment_id"]),
        Index(value = ["category_id"])
    ]
)
data class Exercises(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "is_custom") val isCustom: Boolean = false,
    @ColumnInfo(name = "force_id") val forceId: Int? = null,
    @ColumnInfo(name = "level_id") val levelId: Int? = null,
    @ColumnInfo(name = "mechanic_id") val mechanicId: Int? = null,
    @ColumnInfo(name = "equipment_id") val equipmentId: Int? = null,
    @ColumnInfo(name = "category_id") val categoryId: Int? = null
)

package com.sinya.projects.sportsdiary.domain.model

import com.sinya.projects.sportsdiary.data.database.entity.CategoryTranslation
import com.sinya.projects.sportsdiary.data.database.entity.EquipmentTranslation
import com.sinya.projects.sportsdiary.data.database.entity.ForceTranslation
import com.sinya.projects.sportsdiary.data.database.entity.LevelTranslation
import com.sinya.projects.sportsdiary.data.database.entity.MechanicTranslation

data class ExerciseEditItem(
    val id: Int? = null,
    val name: String = "",
    val isError: Boolean = false,
    val rule: String = "",
    val description: String = "",
    val force: ForceTranslation?,
    val level: LevelTranslation?,
    val mechanic: MechanicTranslation?,
    val equipment: EquipmentTranslation?,
    val category: CategoryTranslation?
)
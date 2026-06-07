package com.sinya.projects.sportsdiary.core.domain.model

import com.sinya.projects.sportsdiary.core.data.dataBase.entity.CategoryTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.EquipmentTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.ForceTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.LevelTranslation
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.MechanicTranslation

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
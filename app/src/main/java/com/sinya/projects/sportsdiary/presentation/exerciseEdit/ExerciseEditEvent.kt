package com.sinya.projects.sportsdiary.presentation.exerciseEdit

import com.sinya.projects.sportsdiary.data.database.entity.CategoryTranslation
import com.sinya.projects.sportsdiary.data.database.entity.EquipmentTranslation
import com.sinya.projects.sportsdiary.data.database.entity.ForceTranslation
import com.sinya.projects.sportsdiary.data.database.entity.LevelTranslation
import com.sinya.projects.sportsdiary.data.database.entity.MechanicTranslation
import com.sinya.projects.sportsdiary.presentation.categoryPage.CategoryPageEvent

sealed interface ExerciseEditEvent {
    data class OnNameChange(val s: String) : ExerciseEditEvent
    data class OnDescriptionChange(val s: String) : ExerciseEditEvent
    data class OnRuleChange(val s: String) : ExerciseEditEvent

    data class MuscleToggle(val id: Int) : ExerciseEditEvent
    data class MuscleToggleValue(val id: Int, val value: Int) : ExerciseEditEvent
    data class OnSelectedCategory(val item: CategoryTranslation?) : ExerciseEditEvent
    data class OnSelectedForce(val item: ForceTranslation?) : ExerciseEditEvent
    data class OnSelectedMechanic(val item: MechanicTranslation?) : ExerciseEditEvent
    data class OnSelectedEquipment(val item: EquipmentTranslation?) : ExerciseEditEvent
    data class OnSelectedLevel(val item: LevelTranslation?) : ExerciseEditEvent
    data class OpenDialogGuide(val title: String, val descr: String) : ExerciseEditEvent
    data object OpenBottomSheetTraining : ExerciseEditEvent

    data object DialogShown : ExerciseEditEvent

    data class Error(val message: String) : ExerciseEditEvent
    data class DeleteMuscle(val id: Int) : ExerciseEditEvent

    data object Save : ExerciseEditEvent
}
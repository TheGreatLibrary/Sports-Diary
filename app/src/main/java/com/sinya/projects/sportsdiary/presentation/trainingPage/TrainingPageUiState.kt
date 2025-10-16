package com.sinya.projects.sportsdiary.presentation.trainingPage

import androidx.room.ColumnInfo
import com.sinya.projects.sportsdiary.data.database.entity.Trainings
import com.sinya.projects.sportsdiary.data.database.entity.TypeTraining
import java.time.LocalDate

sealed class TrainingPageUiState {
    data object Loading : TrainingPageUiState()
    data class Success(
        val id: Int? = null,
        val title: String = "",
        val bottomSheetCategoryStatus: Boolean = false,
        val bottomSheetTrainingStatus: Boolean = false,
        val category: TypeTraining,
        val date: String = LocalDate.now().toString(),
        val items: List<ExerciseItem> = emptyList(),
        val categories: List<TypeTraining> = emptyList()
    ) : TrainingPageUiState()
    data class Error(val message: String) : TrainingPageUiState()
}

data class ExerciseRow(
    val id: Int,
    val title: String,
    @ColumnInfo(name = "count_result") val countResult: String,   // "10/12/15/12"
    @ColumnInfo(name = "weight_result") val weightResult: String, // "20/25/25/30"
    val unitMeasure: String = "кг"
)

fun ExerciseRow.toItem(): ExerciseItem = ExerciseItem(
    id = id,
    title = title,
    countList = countResult.split('/'),   // -> List<String>
    weightList = weightResult.split('/'),
    unitMeasure = unitMeasure
)
data class ExerciseItemOfList(val id: Int, val name: String)


data class ExerciseItem(
    val id: Int,
    val title: String,
    val countList: List<String>,
    val weightList: List<String>,
    val unitMeasure: String = "кг",
)

sealed interface TrainingPageUiEvent {
    data class Save(val exit: () -> Unit) : TrainingPageUiEvent
    data class AddExercise(val title: String,) : TrainingPageUiEvent
    data class OpenBottomSheetCategory(val state: Boolean) : TrainingPageUiEvent
    data class OpenBottomSheetTraining(val state: Boolean) : TrainingPageUiEvent
    data class Delete(val id: Int) : TrainingPageUiEvent
    data class DeleteSet(val id: Int, val index: Int) : TrainingPageUiEvent
    data class AddSet(val id: Int) : TrainingPageUiEvent
    data class OnSelectedCategory(val name: TypeTraining) : TrainingPageUiEvent
    data class EditSet(val exId: Int, val index: Int, val value: String?, val valState: Boolean) : TrainingPageUiEvent
    data object UpdateCategories : TrainingPageUiEvent
    data object UpdateListTraining : TrainingPageUiEvent
}

data class TrainingEntity(
    val id: Int? = null,
    val title: String,
    val category: TypeTraining,
    val date: String,
    val items: List<ExerciseItem>
)

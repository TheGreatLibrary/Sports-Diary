package com.sinya.projects.sportsdiary.presentation.trainingPage

import androidx.room.ColumnInfo
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
        val categories: List<TypeTraining> = emptyList(),
        val dialogContent: ExerciseDialogContent? = null,
    ) : TrainingPageUiState()
    data class Error(val message: String) : TrainingPageUiState()
}

data class ExerciseDialogContent(
    val id: Int,
    val name: String,
    val description: String
)

data class ExerciseRow(
    val id: Int,
    val title: String,
    @ColumnInfo(name = "count_result") val countResult: String,   // "10/12/15/12"
    @ColumnInfo(name = "weight_result") val weightResult: String, // "20/25/25/30"
)

fun ExerciseRow.toItem(): ExerciseItem = ExerciseItem(
    id = id,
    title = title,
    countList = countResult.split('/'),   // -> List<String>
    weightList = weightResult.split('/'),
)

data class ExerciseItem(
    val id: Int,
    val title: String,
    val countList: List<String> = listOf("0", "0", "0", "0"),
    val weightList: List<String> = listOf("0", "0", "0", "0"),
)

data class ExerciseItemWithoutList(
    val id: Int,
    val title: String,
    val unitMeasure: String = "кг",
)

data class TrainingEntity(
    val id: Int? = null,
    val title: String,
    val category: TypeTraining,
    val date: String,
    val items: List<ExerciseItem>
)

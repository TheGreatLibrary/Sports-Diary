package com.sinya.projects.sportsdiary.widgets

import android.util.Log
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.data.database.repository.MorningRepository
import jakarta.inject.Inject

class MarkDayMorningExerciseUseCase @Inject constructor(
    private val repository: MorningRepository
) {
    suspend operator fun invoke(item: DataMorning) {
        Log.d("MarkExerciseUseCase", "Marking today as complete")
        repository.insertMorning(item)
        Log.d("MarkExerciseUseCase", "Marked!")
    }
}
package com.sinya.projects.domain.useCase

import android.util.Log
import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.repository.MorningRepository
import java.time.LocalDate
import javax.inject.Inject

class MarkDayMorningExerciseWidgetUseCase @Inject constructor(
    private val repository: MorningRepository
) {
    suspend operator fun invoke(morningState: Boolean, item: MorningEntry) {
        val date = LocalDate.now().toString()
        val newItem = repository.getMorningByDate(date).getOrNull()

        if (!morningState) newItem.let {
            Log.d("MarkExerciseUseCase", "Marking today as complete")
            repository.insertMorning(item).getOrNull()
            Log.d("MarkExerciseUseCase", "Marked!")
        }
        else newItem?.let {
            Log.d("MarkExerciseUseCase", "Delete today as complete")
            repository.deleteMorning(newItem).getOrNull()
            Log.d("MarkExerciseUseCase", "Deleted!")
        }
    }
}
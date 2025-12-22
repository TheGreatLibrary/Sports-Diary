package com.sinya.projects.sportsdiary.widgets.calendarWidget

import android.util.Log
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.domain.repository.MorningRepository
import jakarta.inject.Inject
import java.time.LocalDate

class MarkDayMorningExerciseUseCase @Inject constructor(
    private val repository: MorningRepository
) {
    suspend operator fun invoke(morningState: Boolean, item: DataMorning) {

        val date = LocalDate.now().toString()
        val newItem = repository.getMorningByDate(date)

        if (!morningState) newItem.let {
            Log.d("MarkExerciseUseCase", "Marking today as complete")
            repository.insertMorning(item)
            Log.d("MarkExerciseUseCase", "Marked!")
        }
        else newItem?.let {
            Log.d("MarkExerciseUseCase", "Delete today as complete")
            repository.deleteMorning(newItem)
            Log.d("MarkExerciseUseCase", "Deleted!")
        }
    }
}
package com.sinya.projects.sportsdiary.domain.model

import com.sinya.projects.sportsdiary.domain.enums.SortMode

sealed class ModeOfSorting(val mode: SortMode) {
    data class TimeMode(
        val year: Int = -1,
        val month: Int = -1
    ) : ModeOfSorting(SortMode.TIME)

    data class MuscleMode(
        val category: String
    ) : ModeOfSorting(SortMode.MUSCLE)
}
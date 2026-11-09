package com.sinya.projects.widgets.calendar

import com.sinya.projects.domain.providers.MorningRoutineIdProvider
import com.sinya.projects.domain.useCase.GetWidgetDataUseCase
import com.sinya.projects.domain.useCase.MarkDayMorningExerciseWidgetUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun morningRoutineIdProvider(): MorningRoutineIdProvider
    fun getWidgetDataUseCase(): GetWidgetDataUseCase
    fun markDayMorningExerciseUseCase(): MarkDayMorningExerciseWidgetUseCase
}
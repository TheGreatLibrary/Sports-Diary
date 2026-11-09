package com.sinya.projects.domain.useCase

import com.sinya.projects.domain.model.CalendarWidgetUiState
import com.sinya.projects.domain.model.DayOfMonth
import com.sinya.projects.domain.model.MonthCalendarResult
import com.sinya.projects.domain.repository.MorningRepository
import com.sinya.projects.domain.repository.TrainingRepository
import com.sinya.projects.domain.utils.parseDateToStringDB
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class GetWidgetDataUseCase @Inject constructor(
    private val morningRepository: MorningRepository,
    private val trainingRepository: TrainingRepository
) {
    private suspend fun getMonthCalendarWithInfo(date: LocalDateTime): MonthCalendarResult {
        val daysCount = 7

        val start = date.toLocalDate()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .atStartOfDay()

        val finish = start.plusDays((daysCount - 1).toLong())
            .withHour(23).withMinute(59).withSecond(59)

        return combine(
            morningRepository.getList(parseDateToStringDB(start), parseDateToStringDB(finish)),
            trainingRepository.getList(parseDateToStringDB(start), parseDateToStringDB(finish)),
        ) { morningList, trainingList ->

            val morningDates = morningList.map { it.date.take(10) }.toHashSet()
            val trainingDates = trainingList.map { it.date.take(10) }.toHashSet()
            val result = ArrayList<DayOfMonth>(daysCount)
            var d = start

            repeat(daysCount) {
                val dayKey = parseDateToStringDB(d).take(10)
                val isCurrentMonthCell = d.monthValue == date.monthValue && d.year == date.year

                result += DayOfMonth(
                    currentMonth = isCurrentMonthCell,
                    date = d,
                    trainingState = dayKey in trainingDates,
                    morningState = dayKey in morningDates
                )
                d = d.plusDays(1)
            }

            MonthCalendarResult(days = result)
        }.first()
    }

    suspend operator fun invoke(): CalendarWidgetUiState {
        val date = LocalDateTime.now()
        val result = getMonthCalendarWithInfo(date)

        return CalendarWidgetUiState(
            date = date,
            monthDays = result.days
        )
    }
}
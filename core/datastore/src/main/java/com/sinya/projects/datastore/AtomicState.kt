package com.sinya.projects.datastore

import com.sinya.projects.domain.enums.Gender
import com.sinya.projects.domain.enums.TypeLanguages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

internal class AtomicState<T>(initial: T) {

    private val flow = MutableStateFlow(initial)

    val value: T get() = flow.value
    val state: StateFlow<T> = flow

    fun update(block: (T) -> T) {
        flow.update(block)
    }

    fun set(new: T) {
        flow.value = new
    }
}


internal data class OptionalPrefs(
    val gender: Gender = Gender.MALE,
    val birthDateMillis: LocalDate = LocalDate.of(2008, 1, 1),
    val targetWeight: Float = 75f,
    val targetStepAtDay: Int = 10000,
    val targetSleepAtDay: Int = 480,
    val targetCaloriesAtDay: Int = 400,
    val autoCalculateMaxHr: Boolean = true,
    val maxHeartRate: Int = 100
)

internal data class CriticalPrefs(
    val dark: Boolean = true,
    val map7: Boolean = true,
    val language: String = TypeLanguages.RU.code,
    val trainingWarning: Boolean = true,
    val planMorningId: Int? = null
)
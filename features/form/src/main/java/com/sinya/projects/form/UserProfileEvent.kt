package com.sinya.projects.form

import com.sinya.projects.domain.enums.Gender
import java.time.LocalDateTime

internal sealed interface UserProfileEvent {
    data class OnGenderSelected(val gender: Gender) : UserProfileEvent
    data class OnDateSelected(val date: LocalDateTime) : UserProfileEvent
    data class OnWeightSelected(val weight: Float) : UserProfileEvent
    data class OnStepsSelected(val steps: Int) : UserProfileEvent
    data class OnSleepSelected(val minutes: Int) : UserProfileEvent
    data class OnCaloriesSelected(val it: Int) : UserProfileEvent
    data class OnHeartRateSelected(val auto: Boolean, val manual: Int) : UserProfileEvent

}
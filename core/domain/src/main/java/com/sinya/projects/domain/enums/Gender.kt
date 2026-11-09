package com.sinya.projects.domain.enums

import androidx.annotation.StringRes
import com.sinya.projects.domain.R

enum class Gender(@StringRes val title: Int) {
    MALE(R.string.male),
    FEMALE(R.string.women);

    companion object {
        fun getGenderByValue(v: Int): Gender {
            return when (v) {
                0 -> MALE
                else -> FEMALE
            }
        }
    }
}
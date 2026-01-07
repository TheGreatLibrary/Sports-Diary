package com.sinya.projects.sportsdiary.domain.enums

import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.domain.model.RadioItem

enum class SortMode(
    val code: Int,
    val radioItem: RadioItem<Int?>
) {
    TIME(code = 0, radioItem = RadioItem(null, R.drawable.train_time)),
    MUSCLE(code = 1, radioItem = RadioItem(null, R.drawable.train_muscul));

    companion object {
        fun fromIndex(index: Int?): SortMode =
            SortMode.entries.find { it.code == index } ?: TIME

        fun getList() : List<RadioItem<Int?>> =
            SortMode.entries.map { it.radioItem }
    }
}
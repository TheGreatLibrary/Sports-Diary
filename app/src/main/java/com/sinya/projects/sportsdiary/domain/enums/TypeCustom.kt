package com.sinya.projects.sportsdiary.domain.enums

import com.sinya.projects.sportsdiary.R

enum class TypeCustom(
    val state: Boolean?,
    val stringRes: Int
) {
    ALL(null, R.string.all),
    BASED(false, R.string.base_exercise),
    CUSTOM(true, R.string.custom);

    companion object {
        fun getType(state: Boolean?): TypeCustom {
            return when (state) {
                true -> TypeCustom.CUSTOM
                false -> TypeCustom.BASED
                else -> TypeCustom.ALL
            }
        }
    }
}
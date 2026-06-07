package com.sinya.projects.sportsdiary.core.domain.model

import com.sinya.projects.sportsdiary.core.domain.enums.TypeCustom

sealed interface SortParam {
    data class Year(val value: Int) : SortParam
    data class Month(val value: Int) : SortParam
    data class Category(val value: String?) : SortParam
    data class Equipment(val value: String?) : SortParam
    data class Level(val value: String?) : SortParam
    data class Custom(val value: TypeCustom) : SortParam
}
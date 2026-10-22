package com.sinya.projects.ui.components.sort

sealed interface SortParam {
    data class Year(val value: Int) : SortParam
    data class Month(val value: Int) : SortParam
    data class Category(val value: String?) : SortParam
    data class Equipment(val value: String?) : SortParam
    data class Level(val value: String?) : SortParam
    data class Custom(val value: TypeCustom) : SortParam
}
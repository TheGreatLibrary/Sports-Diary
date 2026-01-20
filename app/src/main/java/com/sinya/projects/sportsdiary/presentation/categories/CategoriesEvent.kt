package com.sinya.projects.sportsdiary.presentation.categories

sealed interface CategoriesEvent {
    data class OpenDialog(val id: Int?) : CategoriesEvent
    data object DeleteCategory : CategoriesEvent
    data object ReloadData : CategoriesEvent
    data object OnErrorShown : CategoriesEvent
}
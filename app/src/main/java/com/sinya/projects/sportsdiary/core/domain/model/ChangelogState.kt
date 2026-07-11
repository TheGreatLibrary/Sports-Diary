package com.sinya.projects.sportsdiary.core.domain.model

sealed interface ChangelogState {
    data object Loading : ChangelogState
    data class Success(val data: ChangelogResponse) : ChangelogState
    data class Error(val message: String) : ChangelogState
}
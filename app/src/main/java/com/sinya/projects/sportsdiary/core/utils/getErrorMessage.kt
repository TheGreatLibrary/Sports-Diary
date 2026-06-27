package com.sinya.projects.sportsdiary.core.utils

import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.error.EmptyTitleError
import com.sinya.projects.sportsdiary.core.domain.error.SavedChanges
import com.sinya.projects.sportsdiary.core.domain.error.SavingProcess
import com.sinya.projects.sportsdiary.core.domain.error.TitleExistedError

fun Throwable.getErrorMessage(): Int = when (this) {
    is SavedChanges -> R.string.saved
    is SavingProcess -> R.string.saving
    is EmptyTitleError -> R.string.title_is_empty
    is TitleExistedError -> R.string.title_already_existed
    else -> R.string.error_unknown
}

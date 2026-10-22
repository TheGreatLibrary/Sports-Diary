package com.sinya.projects.domain.utils

import com.sinya.projects.domain.R
import com.sinya.projects.domain.error.EmptyTitleError
import com.sinya.projects.domain.error.SavedChanges
import com.sinya.projects.domain.error.SavingProcess
import com.sinya.projects.domain.error.TitleExistedError

fun Throwable.getErrorMessage(): Int = when (this) {
    is SavedChanges -> R.string.saved
    is SavingProcess -> R.string.saving
    is EmptyTitleError -> R.string.title_is_empty
    is TitleExistedError -> R.string.title_already_existed
    else -> R.string.error_unknown
}

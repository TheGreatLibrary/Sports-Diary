package com.sinya.projects.sportsdiary.core.utils

fun Throwable.getErrorMessage(): Int = when (this) {
    else -> 1
//    is DefinitionNotFoundException -> R.string.error_definition_not_found
//    is InvalidNicknameException -> R.string.error_invalid_nickname
//    is NoInternetException -> R.string.error_no_internet
//    is UserHasNotProfileException -> R.string.error_no_profile
//    is UserNotAuthenticatedException -> R.string.error_not_authenticated
//    is WordNotFoundException -> R.string.error_word_not_found
//    is SessionRestoreException -> R.string.error_session_restore
//    else -> R.string.error_unknown
}

package com.sinya.projects.domain.model

interface UndoableAction<S> {
    fun apply(state: S): S
    fun revert(state: S): S
}
package com.sinya.projects.domain.utils

import com.sinya.projects.domain.model.UndoableAction


class UndoManager<S> {
    private val history = ArrayDeque<UndoableAction<S>>()
    private var cursor = -1

    fun push(action: UndoableAction<S>) {
        // всё что впереди курсора — стираем
        while (history.size > cursor + 1) history.removeLast()
        history.addLast(action)
        cursor = history.lastIndex
    }

    fun undo(state: S): S? {
        if (cursor < 0) return null
        val action = history[cursor]
        cursor--
        return action.revert(state)
    }

    fun redo(state: S): S? {
        if (cursor >= history.lastIndex) return null
        cursor++
        return history[cursor].apply(state)
    }

    val canUndo get() = cursor >= 0
    val canRedo get() = cursor < history.lastIndex
}
package com.sinya.projects.pagecategory.analyze

internal sealed interface RoutinePageAnalyzeEvent {
    data object OnBodyInfoClick : RoutinePageAnalyzeEvent
}
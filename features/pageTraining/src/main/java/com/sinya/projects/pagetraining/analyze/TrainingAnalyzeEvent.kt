package com.sinya.projects.pagetraining.analyze

sealed interface TrainingAnalyzeEvent {
    data object OnBodyInfoClick : TrainingAnalyzeEvent

}
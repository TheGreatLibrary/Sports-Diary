package com.sinya.projects.editexercise.subScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.DataExerciseMuscles
import com.sinya.projects.domain.model.ExerciseMusclesData
import com.sinya.projects.editexercise.ExerciseEditEvent
import com.sinya.projects.ui.R.string
import com.sinya.projects.ui.components.map.BodyCard
import com.sinya.projects.ui.components.CustomButton

@Composable
internal fun MuscleScreen(
    list: List<ExerciseMusclesData>,
    onEvent: (ExerciseEditEvent) -> Unit,
    openMenu: () -> Unit
) {
    val listMuscle = remember(list) {
        list.map {
            DataExerciseMuscles(
                muscleId = it.muscleId,
                name = it.name,
                value = 1f,
                level = it.value,
                centerX = 0f,
                centerY = 0f,
                radiusScale = 0f,
                res = 0
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BodyCard(
            title = stringResource(string.muscle_title),
            list = listMuscle,
            withText = false,
            onInfoClick = { },
            withColorText = true
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CustomButton(
                onClick = openMenu,
                text = stringResource(string.add_exercise),
            )
        }
    }
}
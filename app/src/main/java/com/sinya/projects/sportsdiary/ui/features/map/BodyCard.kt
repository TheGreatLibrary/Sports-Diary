package com.sinya.projects.sportsdiary.ui.features.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.core.domain.enums.Muscles
import com.sinya.projects.sportsdiary.ui.features.map.components.MuscleGroup
import com.sinya.projects.sportsdiary.ui.features.map.model.DataExerciseMuscles
import com.sinya.projects.sportsdiary.ui.features.map.components.BodyMuscles
import com.sinya.projects.sportsdiary.ui.features.chart.components.ChartHeader
import com.sinya.projects.sportsdiary.ui.features.map.components.MusclesNameRow
import com.sinya.projects.sportsdiary.ui.features.map.components.ProgressBarMuscles

@Composable
fun BodyCard(
    title: String = stringResource(R.string.work_muscles_for_week),
    list: List<DataExerciseMuscles>,
    withText: Boolean = true,
    onInfoClick: () -> Unit,
    withColorText: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChartHeader(
            title = title,
            onInfoClick = onInfoClick
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BodyMuscles(
                bodyIcon = painterResource(R.drawable.body_body_lines),
                list = Muscles.getData(list, true)
            )
            BodyMuscles(
                bodyIcon = painterResource(R.drawable.body_body_lines_back),
                list = Muscles.getData(list, false),
            )
        }

        ProgressBarMuscles()

        if (withText) {
            MusclesNameRow(
                list = list.map { Pair(it.muscleId, it.name) },
                getText = { first, second -> "$first - $second" }
            )
        }

        if (withColorText) {
            val primaryM = remember {
                list.filter { it.level == 2 }.joinToString(", ") { it.name }
            }
            val secondaryM = remember {
                list.filter { it.level == 1 }.joinToString(", ") { it.name }
            }

            if (primaryM.isNotEmpty()) {
                MuscleGroup(
                    label = stringResource(R.string.primary_muscle),
                    muscles = primaryM,
                    isPrimary = true
                )
            }
            if (secondaryM.isNotEmpty()) {
                MuscleGroup(
                    label = stringResource(R.string.secondary_muscle),
                    muscles = secondaryM,
                    isPrimary = false
                )
            }
        }
    }
}

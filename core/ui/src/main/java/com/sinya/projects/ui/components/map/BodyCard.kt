package com.sinya.projects.ui.components.map

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
import com.sinya.projects.domain.model.DataExerciseMuscles
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.HeaderWithInfo
import com.sinya.projects.ui.components.map.components.BodyMuscles
import com.sinya.projects.ui.components.map.components.MuscleGroup
import com.sinya.projects.ui.components.map.components.MusclesNameRow
import com.sinya.projects.ui.components.map.components.ProgressBarMuscles
import com.sinya.projects.ui.components.map.utils.Muscles
import com.sinya.projects.ui.theme.AppGradients
import com.sinya.projects.ui.theme.LocalAppGradients

@Composable
fun BodyCard(
    title: String = stringResource(R.string.work_muscles_for_week),
    list: List<DataExerciseMuscles>,
    withText: Boolean = true,
    onInfoClick: () -> Unit,
    withColorText: Boolean = false,
    gradients: AppGradients = LocalAppGradients.current
) {
    val listMuscle = remember(list) { list.map { Pair(it.muscleId, it.name) } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                shape = MaterialTheme.shapes.small,
                brush = gradients.basedCardGradient
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderWithInfo(
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
                bodyLineIcon = painterResource(R.drawable.body_body),
                list = Muscles.getData(list, true)
            )
            BodyMuscles(
                bodyIcon = painterResource(R.drawable.body_body_lines_back),
                bodyLineIcon = painterResource(R.drawable.body_body_back),
                list = Muscles.getData(list, false),
            )
        }

        ProgressBarMuscles()

        if (withText) {
            MusclesNameRow(
                list = listMuscle,
                getText = { first, second -> "$first - $second" }
            )
        }

        if (withColorText) {
            val primaryM = remember(list) {
                list.filter { it.level == 2 }.joinToString(", ") { it.name }
            }
            val secondaryM = remember(list) {
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

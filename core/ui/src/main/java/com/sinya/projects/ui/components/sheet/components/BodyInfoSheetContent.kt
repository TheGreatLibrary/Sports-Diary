package com.sinya.projects.ui.components.sheet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.map.components.MuscleMap
import com.sinya.projects.ui.components.map.components.MusclesNameRow

@Composable
fun BodyInfoSheetContent(
    listMuscles: List<Pair<Int, String>>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.muscles_of_body),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.hot_map_description),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MuscleMap(
                bodyIcon = painterResource(R.drawable.body_body_lines),
                bodyLines = painterResource(R.drawable.body_lines),
            )
            MuscleMap(
                bodyIcon = painterResource(R.drawable.body_body_lines_back),
                bodyLines = painterResource(R.drawable.body_lines_back),
            )
        }
        MusclesNameRow(
            list = listMuscles,
            getText = { first, second -> "$first - $second" }
        )
        Spacer(Modifier.height(40.dp))
    }
}
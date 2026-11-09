package com.sinya.projects.ui.components.sheet.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.domain.model.ExerciseWithMuscles
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.ExerciseList
import com.sinya.projects.ui.components.SmartSheetHeader
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import com.sinya.projects.ui.components.smartHeader.util.rememberSmartHeaderManager
import com.sinya.projects.ui.components.smartHeader.util.smartHeader
import com.sinya.projects.ui.components.sort.FilterBuilder
import com.sinya.projects.ui.components.sort.ModeOfSorting


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSheetContent(
    query: String,
    onQueryChange: (String) -> Unit,
    onClickSuccess: () -> Unit,
    onCancelClick: () -> Unit,
    filtered: List<ExerciseWithMuscles>,
    onToggle: (Int) -> Unit,
    onModeClick: (ModeOfSorting, Any) -> Unit,
    modesFlattened: List<Pair<FilterBuilder<Any>, ModeOfSorting>>
) {
    val manager = rememberSmartHeaderManager()
    val smartHeader = manager.rememberScrollDirection()
    val stateBottomSheet = remember { mutableStateOf(false) }
    val density = LocalDensity.current

    val headerVisibleHeightDp = with(density) {
        (smartHeader.headerHeightPx + smartHeader.headerOffsetPx)
            .coerceAtLeast(0f)
            .toDp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(smartHeader.headerScrollConnection),
        contentAlignment = Alignment.BottomCenter
    ) {
        ExerciseList(
            filtered = filtered,
            onToggle = onToggle,
            listState = smartHeader.listState,
            contentPadding = PaddingValues(
                top = headerVisibleHeightDp + 8.dp,
                bottom = 80.dp // Добавляем отступ снизу, чтобы плавающие кнопки не перекрыли последние элементы
            ),
        )

        // 2. Хедер. Выравниваем строго по верху.
        SmartSheetHeader(
            modifier = Modifier
                .align(Alignment.TopCenter) // Прибиваем к верху
                .smartHeader(manager),
            onOptionSelected = { stateBottomSheet.value = !stateBottomSheet.value },
            query = query,
            title = stringResource(R.string.choose_exercises),
            onQueryChange = onQueryChange,
        )

        // 3. Кнопки. Выравниваем строго по низу.

            SheetButtons(
                list = listOf(
                    painterResource(R.drawable.icon_back) to onCancelClick,
                    painterResource(R.drawable.ic_plus) to onClickSuccess
                )
            )

        if (stateBottomSheet.value) {
            CustomModalSheet(
                onDismiss = { stateBottomSheet.value = !stateBottomSheet.value }
            ) {
                SortedExercisesSheetContent(
                    onOptionSelected = onModeClick,
                    modesFlattened = modesFlattened
                )
            }
        }
    }
}
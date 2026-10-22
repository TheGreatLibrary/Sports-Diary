package com.sinya.projects.editexercise.subScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.ui.R
import com.sinya.projects.ui.components.PointRuleLine
import com.sinya.projects.ui.components.textField.CustomTextField
import com.sinya.projects.ui.components.CustomButton

private const val STEP_DELIMITER = "\n\n"

@Composable
internal fun InstructionScreen(
    rule: String,
    onRuleChange: (String) -> Unit,
) {
    // Безопасно разбиваем строку на список шагов.
    // Если строка пустая, создаем список с одним пустым полем.
    val steps = remember(rule) {
        if (rule.isBlank()) listOf("") else rule.split(STEP_DELIMITER)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(
            items = steps,
            // key нужен, чтобы Compose не путал поля при удалении шагов из середины
            key = { index, _ -> "step_$index" }
        ) { index, stepText ->
            StepInputCard(
                stepNumber = index + 1,
                text = stepText,
                onTextChange = { newText ->
                    val mutableSteps = steps.toMutableList()
                    mutableSteps[index] = newText
                    onRuleChange(mutableSteps.joinToString(STEP_DELIMITER))
                },
                onRemove = {
                    val mutableSteps = steps.toMutableList()
                    mutableSteps.removeAt(index)
                    if (mutableSteps.isEmpty()) mutableSteps.add("") // Оставляем хотя бы одно пустое поле
                    onRuleChange(mutableSteps.joinToString(STEP_DELIMITER))
                },
                canRemove = steps.size > 1
            )
        }

        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CustomButton(
                    onClick = {
                        val mutableSteps = steps.toMutableList()
                        mutableSteps.add("")
                        onRuleChange(mutableSteps.joinToString(STEP_DELIMITER))
                    },
                    text = stringResource(R.string.add_exercise),
                )
            }
//            OutlinedButton(
//                onClick = {
//                    val mutableSteps = steps.toMutableList()
//                    mutableSteps.add("")
//                    onRuleChange(mutableSteps.joinToString(STEP_DELIMITER))
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp),
//                shape = MaterialTheme.shapes.medium
//            ) {
//
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Добавить шаг")
//            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepInputCard(
    stepNumber: Int,
    text: String,
    onTextChange: (String) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PointRuleLine(stepNumber)

        CustomTextField(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shape = MaterialTheme.shapes.small
                ),
            value = text,
            onValueChange = onTextChange,
            placeholder = "Опишите действие...",
            maxLines = 7,
            trailingIcon = {
                IconButton(onClick = { if (canRemove) onRemove() }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.morn_trash),
                        contentDescription = "Удалить шаг",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            },
            contentColor = Color.White
        )
    }
}
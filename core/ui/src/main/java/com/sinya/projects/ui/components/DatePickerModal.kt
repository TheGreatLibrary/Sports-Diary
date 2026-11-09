package com.sinya.projects.ui.components
//
//import androidx.compose.material3.DatePicker
//import androidx.compose.material3.DatePickerDialog
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.rememberDatePickerState
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.scale
//import androidx.compose.ui.window.DialogProperties
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DatePickerModal(
//    onDateSelected: (Long?) -> Unit,
//    onDismiss: () -> Unit
//) {
//    val datePickerState = rememberDatePickerState()
//
//    DatePickerDialog(
//        onDismissRequest = onDismiss,
//        modifier = Modifier.scale(0.9f),
//        shape = MaterialTheme.shapes.small,
//        confirmButton = {
//            TextButton(onClick = {
//                onDateSelected(datePickerState.selectedDateMillis)
//                onDismiss()
//            }) {
//                Text("OK")
//            }
//        },
//        properties = DialogProperties(usePlatformDefaultWidth = true),
//        dismissButton = {
//            TextButton(onClick = onDismiss) {
//                Text("Cancel")
//            }
//        }
//    ) {
//        DatePicker(state = datePickerState)
//    }
//}

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerModal(
    initialDateTime: LocalDateTime = LocalDateTime.now(),
    onDateTimeSelected: (LocalDateTime) -> Unit,
    withTime: Boolean = true,
    onDismiss: () -> Unit
) {
    // Шаг 1 = выбор даты, Шаг 2 = выбор времени
    var showTimePicker by remember { mutableStateOf(false) }

    // DatePicker в Material3 работает строго в UTC на уровне дней
    val initialDateMillis = remember(initialDateTime) {
        initialDateTime.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )

    val timePickerState = rememberTimePickerState(
        initialHour = initialDateTime.hour,
        initialMinute = initialDateTime.minute,
        is24Hour = true
    )

    if (!showTimePicker) {
        // ШАГ 1: Выбор даты
        DatePickerDialog(
            onDismissRequest = onDismiss,
            modifier = Modifier.scale(0.9f),
            shape = MaterialTheme.shapes.small,
            properties = DialogProperties(usePlatformDefaultWidth = true),
            confirmButton = {
                TextButton(
                    onClick = {
                        if (withTime) showTimePicker = true else {
                            val selectedMillis = datePickerState.selectedDateMillis
                            if (selectedMillis != null) {
                                val pickedDate = Instant.ofEpochMilli(selectedMillis)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
                                val pickedTime = LocalTime.of(0, 0)

                                onDateTimeSelected(LocalDateTime.of(pickedDate, pickedTime))
                            }
                            onDismiss()
                        }
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    if (withTime) Text("Далее") else Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    } else if (withTime) {
        // ШАГ 2: Выбор времени
        AlertDialog(
            onDismissRequest = onDismiss,
            shape = MaterialTheme.shapes.small,
            title = { Text("Выберите время тренировки") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Циферблат. Если мало места на экране, замени на TimeInput(state = timePickerState)
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            // Важно: достаем дату именно через ZoneOffset.UTC, чтобы часовой пояс не сдвинул день назад!
                            val pickedDate = Instant.ofEpochMilli(selectedMillis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            val pickedTime =
                                LocalTime.of(timePickerState.hour, timePickerState.minute)

                            onDateTimeSelected(LocalDateTime.of(pickedDate, pickedTime))
                        }
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Назад")
                }
            }
        )
    }
}
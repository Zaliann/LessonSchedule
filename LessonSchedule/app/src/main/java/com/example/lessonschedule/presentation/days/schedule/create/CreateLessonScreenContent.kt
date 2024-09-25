package com.example.lessonschedule.presentation.days.schedule.create

import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.lessonschedule.R
import com.example.lessonschedule.domain.LessonType
import com.example.lessonschedule.presentation.getTitle
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLessonScreenContent(
    state: CreateLessonScreen.State,
    onAction: (CreateLessonScreen.Action) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Добавить к расписанию") },
                navigationIcon = {
                    val navigator = LocalNavigator.current
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                navigator?.pop()
                            },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(state.snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAction(CreateLessonScreen.Action.Create)
                },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = ""
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title.value,
                isError = state.title.isError,
                label = {
                    Text("Название предмета")
                },
                onValueChange = { value ->
                    onAction(CreateLessonScreen.Action.TitleUpdate(value))
                }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.venue.value,
                isError = state.venue.isError,
                label = {
                    Text("Место проведение")
                },
                onValueChange = { value ->
                    onAction(CreateLessonScreen.Action.VenueUpdate(value))
                }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.teacherName.value,
                isError = state.teacherName.isError,
                label = {
                    Text("Имя преподавателя")
                },
                onValueChange = { value ->
                    onAction(CreateLessonScreen.Action.TeacherUpdate(value))
                }
            )

            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    value = state.selectedLessonType.getTitle(),
                    readOnly = true,
                    singleLine = true,
                    onValueChange = {},
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    label = { Text(text = "Тип предмета") }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    LessonType.entries.fastForEach {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = {
                                Text(
                                    text = it.getTitle(),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            onClick = {
                                onAction(CreateLessonScreen.Action.LessonTypeUpdate(it))
                                expanded = false
                            }
                        )
                    }
                }
            }

            var showDatePicker by remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState()

            OutlinedTextField(
                value = state.selectedDate.value
                    ?.let { state.formatter.format(Date(it)) }
                    .orEmpty(),
                isError = state.selectedDate.isError,
                onValueChange = { },
                label = { Text("Дата занятия") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = !showDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable {
                                    datePickerState.selectedDateMillis?.let {
                                        onAction(CreateLessonScreen.Action.DateSelected(it))
                                    }
                                    showDatePicker = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Ok")
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }

            val currentTime = Calendar.getInstance()

            val timePickerState = rememberTimePickerState(
                initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = currentTime.get(Calendar.MINUTE),
                is24Hour = true,
            )

            var isStartTimeDialogVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = state.selectedTimeStart.value
                    ?.let { (hour, minute) -> "$hour:$minute" }
                    .orEmpty(),
                onValueChange = { },
                isError = state.selectedTimeStart.isError,
                label = { Text("Время начала занятия") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { isStartTimeDialogVisible = !isStartTimeDialogVisible }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (isStartTimeDialogVisible) {
                AlertDialog(
                    onDismissRequest = { isStartTimeDialogVisible = false },
                    dismissButton = {
                        TextButton(onClick = { isStartTimeDialogVisible = false }) {
                            Text("Dismiss")
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val startTime = timePickerState.hour to timePickerState.minute
                                onAction(CreateLessonScreen.Action.StartTimeSelected(startTime))
                                isStartTimeDialogVisible = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    text = {
                        TimePicker(
                            state = timePickerState,
                        )
                    }
                )
            }

            val endTimePickerState = rememberTimePickerState(
                initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = currentTime.get(Calendar.MINUTE),
                is24Hour = true,
            )

            var isEndTimeDialogVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = state.selectedTimeEnd.value
                    ?.let { (hour, minute) -> "$hour:$minute" }
                    .orEmpty(),
                onValueChange = { },
                isError = state.selectedTimeEnd.isError,
                label = { Text("Время конца занятия") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { isEndTimeDialogVisible = !isEndTimeDialogVisible }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (isEndTimeDialogVisible) {
                AlertDialog(
                    onDismissRequest = { isEndTimeDialogVisible = false },
                    dismissButton = {
                        TextButton(onClick = { isEndTimeDialogVisible = false }) {
                            Text("Dismiss")
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val endTime = endTimePickerState.hour to endTimePickerState.minute
                                onAction(CreateLessonScreen.Action.EndTimeSelected(endTime))
                                isEndTimeDialogVisible = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    text = {
                        TimePicker(state = endTimePickerState)
                    }
                )
            }
        }
    }
}
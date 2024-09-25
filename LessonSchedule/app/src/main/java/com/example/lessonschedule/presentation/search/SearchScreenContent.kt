package com.example.lessonschedule.presentation.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lessonschedule.presentation.common.LessonItemContent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreenContent(state: SearchScreen.State, onAction: (SearchScreen.Action) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Поиск") },
                actions = {
                    var showDatePicker by remember { mutableStateOf(false) }
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }

                    val dateRangePickerState = rememberDateRangePickerState()

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        val range = Pair(
                                            dateRangePickerState.selectedStartDateMillis,
                                            dateRangePickerState.selectedEndDateMillis
                                        )
                                        onAction(SearchScreen.Action.RangePicked(range))
                                        showDatePicker = false
                                    }
                                ) {
                                    Text("Ok")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Отмена")
                                }
                            }
                        ) {
                            val dateFormatter = remember { DatePickerDefaults.dateFormatter() }
                            DateRangePicker(
                                headline = {
                                    DateRangePickerDefaults.DateRangePickerHeadline(
                                        selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                                        selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                                        displayMode = dateRangePickerState.displayMode,
                                        dateFormatter,
                                        modifier = Modifier
                                    )
                                },
                                state = dateRangePickerState,
                                title = {
                                    Text(
                                        text = "Выберите даты занятий"
                                    )
                                },
                                showModeToggle = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding())

        LazyColumn(
            modifier = contentModifier,
            state = rememberLazyListState(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            contentPadding = remember { PaddingValues(vertical = 8.dp) }
        ) {
            state.searchItems.forEach { (date, lessons) ->
                stickyHeader(key = date) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        text = date,
                        textAlign = TextAlign.Start
                    )
                }

                items(
                    items = lessons,
                    key = { it.id }
                ) {
                    LessonItemContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .animateItem(),
                        item = it,
                        onClick = {},
                        onDismiss = null
                    )
                }
            }
        }
    }
}
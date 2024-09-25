package com.example.lessonschedule.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class InputState<T>(val value: T, val isError: Boolean = false)
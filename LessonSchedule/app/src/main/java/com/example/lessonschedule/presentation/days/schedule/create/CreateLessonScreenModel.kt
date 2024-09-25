package com.example.lessonschedule.presentation.days.schedule.create

import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import com.example.lessonschedule.DependencyHolder
import com.example.lessonschedule.domain.Lesson
import com.example.lessonschedule.domain.LessonType
import com.example.lessonschedule.presentation.base.BaseScreenModel
import com.example.lessonschedule.presentation.models.InputState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.temporal.ChronoUnit

class CreateLessonScreenModel :
    BaseScreenModel<CreateLessonScreen.Action, CreateLessonScreen.State>(
        initialState = CreateLessonScreen.State()
    ) {
    private val repository = DependencyHolder.lessonRepository
    override fun onAction(action: CreateLessonScreen.Action) {
        when (action) {
            is CreateLessonScreen.Action.DateSelected -> onDateSelected(action.date)
            is CreateLessonScreen.Action.LessonTypeUpdate -> onLessonTypeSelected(action.type)
            is CreateLessonScreen.Action.TeacherUpdate -> onTeacherNameUpdate(action.text)
            is CreateLessonScreen.Action.StartTimeSelected -> onStartTimeSelected(action.time)
            is CreateLessonScreen.Action.TitleUpdate -> onTitleUpdate(action.text)
            is CreateLessonScreen.Action.VenueUpdate -> onVenueUpdate(action.text)
            is CreateLessonScreen.Action.EndTimeSelected -> onEndTimeSelected(action.time)
            CreateLessonScreen.Action.Create -> create()
        }
    }

    private fun create() {
        validateState()
        if (state.value.isStateValid.not()) {
            return
        }

        val currentState = state.value
        val dateMillis = currentState.selectedDate.value ?: 0L
        val date = Instant
            .ofEpochMilli(dateMillis)
            .minus(5L, ChronoUnit.HOURS)
        val startAt = currentState.selectedTimeStart.value
            ?.let { (hour, minute) ->
                date
                    .plus(hour.toLong(), ChronoUnit.HOURS)
                    .plus(minute.toLong(), ChronoUnit.MINUTES)
            }
            ?: date

        val endAt = currentState.selectedTimeEnd.value
            ?.let { (hour, minute) ->
                date
                    .plus(hour.toLong(), ChronoUnit.HOURS)
                    .plus(minute.toLong(), ChronoUnit.MINUTES)
            }
            ?: date

        val lesson = Lesson(
            id = 0,
            title = currentState.title.value,
            venue = currentState.venue.value,
            teacherName = currentState.teacherName.value,
            type = currentState.selectedLessonType,
            startAt = startAt,
            endAt = endAt
        )

        ioScope.launch {
            repository.createLesson(lesson)

            withContext(Dispatchers.Main.immediate) {
                val snackbarHostState = state.value.snackbarHostState
                snackbarHostState.showSnackbar(
                    message = "Запись создана",
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
            }
        }

        _state.update {
            CreateLessonScreen.State()
        }
    }

    private fun onDateSelected(date: Long) {
        _state.update {
            it.copy(
                selectedDate = InputState(date)
            )
        }
    }

    private fun onLessonTypeSelected(type: LessonType) {
        _state.update {
            it.copy(selectedLessonType = type)
        }
    }

    private fun onTeacherNameUpdate(name: String) {
        _state.update {
            it.copy(teacherName = InputState(value = name))
        }
    }

    private fun onTitleUpdate(name: String) {
        _state.update {
            it.copy(title = InputState(value = name))
        }
    }

    private fun onVenueUpdate(name: String) {
        _state.update {
            it.copy(venue = InputState(value = name))
        }
    }

    private fun onStartTimeSelected(time: Pair<Int, Int>) {
        _state.update {
            it.copy(selectedTimeStart = InputState(value = time))
        }
    }

    private fun onEndTimeSelected(time: Pair<Int, Int>) {
        _state.update {
            it.copy(selectedTimeEnd = InputState(value = time))
        }
    }

    private fun validateState() {
        val currentState = state.value

        val startTime = currentState.selectedTimeStart.value
        val endTime = currentState.selectedTimeEnd.value

        val isTimeRangeError = startTime == null ||
                endTime == null ||
                (startTime.first * 60 + startTime.second) > (endTime.first * 60 + endTime.second)

        _state.update {
            it.copy(
                title = validateTextInput(it.title),
                teacherName = validateTextInput(it.teacherName),
                venue = validateTextInput(it.venue),
                selectedTimeStart = it.selectedTimeStart.copy(isError = isTimeRangeError),
                selectedTimeEnd = it.selectedTimeEnd.copy(isError = isTimeRangeError),
                selectedDate = it.selectedDate.copy(isError = it.selectedDate.value == null)
            )
        }
    }

    private fun validateTextInput(inputState: InputState<String>): InputState<String> {
        if (inputState.value.isBlank()) {
            return inputState.copy(isError = true)
        }

        return inputState
    }
}
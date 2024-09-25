package com.example.lessonschedule.presentation.days.schedule.create

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.example.lessonschedule.domain.LessonType
import com.example.lessonschedule.presentation.base.BaseScreen
import com.example.lessonschedule.presentation.base.BaseScreenModel
import com.example.lessonschedule.presentation.models.InputState
import java.text.SimpleDateFormat
import java.util.Locale

class CreateLessonScreen : BaseScreen<CreateLessonScreen.Action, CreateLessonScreen.State>() {
    @Immutable
    data class State(
        val title: InputState<String> = InputState(""),
        val venue: InputState<String> = InputState(""),
        val teacherName: InputState<String> = InputState(""),
        val selectedLessonType: LessonType = LessonType.LECTURE,
        val selectedDate: InputState<Long?> = InputState(null),
        val selectedTimeStart: InputState<Pair<Int, Int>?> = InputState(null),
        val selectedTimeEnd: InputState<Pair<Int, Int>?> = InputState(null),
        val snackbarHostState: SnackbarHostState = SnackbarHostState()
    ) {
        val formatter by lazy {
            SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        }
        val isStateValid by lazy {
            listOf(title, venue, teacherName, selectedDate, selectedTimeStart, selectedTimeEnd)
                .all { it.isError.not() }
        }
    }

    @Immutable
    sealed interface Action {
        data class TitleUpdate(val text: String) : Action
        data class VenueUpdate(val text: String) : Action
        data class TeacherUpdate(val text: String) : Action
        data class LessonTypeUpdate(val type: LessonType) : Action
        data class DateSelected(val date: Long) : Action
        data class StartTimeSelected(val time: Pair<Int, Int>) : Action
        data class EndTimeSelected(val time: Pair<Int, Int>) : Action
        data object Create : Action
    }

    @Composable
    override fun Content(state: State, onAction: (Action) -> Unit) {
        CreateLessonScreenContent(state, onAction)
    }

    override fun createModel(): BaseScreenModel<Action, State> =
        CreateLessonScreenModel()
}
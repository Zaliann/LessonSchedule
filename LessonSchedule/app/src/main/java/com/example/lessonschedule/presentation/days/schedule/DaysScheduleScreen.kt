package com.example.lessonschedule.presentation.days.schedule

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.example.lessonschedule.presentation.models.LessonItem
import com.example.lessonschedule.presentation.base.BaseScreen

class DaysScheduleScreen : BaseScreen<DaysScheduleScreen.Action, DaysScheduleScreen.State>() {

    @Immutable
    data class State(
        val lessons: List<Pair<String, List<LessonItem>>> = emptyList(),
        val pagerState: PagerState = PagerState { lessons.size },
        val viewedItem: LessonItem? = null
    )

    @Immutable
    sealed interface Action {
        data class OnItemClick(val item: LessonItem?) : Action
        data class DeleteItem(val item: LessonItem) : Action
        data object CloseViewDialog : Action
    }

    @Composable
    override fun Content(state: State, onAction: (Action) -> Unit) {
        DaysScheduleScreenContent(state, onAction)
    }

    override fun createModel() = DaysScheduleScreenModel()
}
package com.example.lessonschedule.presentation.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.example.lessonschedule.presentation.base.BaseScreen
import com.example.lessonschedule.presentation.models.LessonItem

class SearchScreen : BaseScreen<SearchScreen.Action, SearchScreen.State>() {

    @Immutable
    data class State(
        val searchItems: Map<String, List<LessonItem>> = emptyMap()
    )

    @Immutable
    sealed interface Action {
        data class RangePicked(val range: Pair<Long?, Long?>) : Action
    }

    @Composable
    override fun Content(state: State, onAction: (Action) -> Unit) {
        SearchScreenContent(state, onAction)
    }

    override fun createModel() = SearchScreenModel()

}
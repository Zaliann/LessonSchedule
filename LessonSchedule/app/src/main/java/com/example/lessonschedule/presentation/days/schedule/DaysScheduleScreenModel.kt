package com.example.lessonschedule.presentation.days.schedule

import androidx.compose.foundation.pager.PagerState
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.lessonschedule.DependencyHolder
import com.example.lessonschedule.presentation.base.BaseScreenModel
import com.example.lessonschedule.presentation.models.LessonItem
import com.example.lessonschedule.presentation.models.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.TimeZone


class DaysScheduleScreenModel :
    BaseScreenModel<DaysScheduleScreen.Action, DaysScheduleScreen.State>(
        initialState = DaysScheduleScreen.State()
    ) {
    private val repository = DependencyHolder.lessonRepository

    init {
        observeLessons()
    }

    override fun onAction(action: DaysScheduleScreen.Action) {
        when (action) {
            DaysScheduleScreen.Action.CloseViewDialog -> setViewedItem(null)
            is DaysScheduleScreen.Action.OnItemClick -> setViewedItem(action.item)
            is DaysScheduleScreen.Action.DeleteItem -> deleteLesson(action.item)
        }
    }

    private fun observeLessons() {
        repository.lessons
            .map { lessons ->

                val now = Instant
                    .now()
                    .truncatedTo(ChronoUnit.DAYS)
                    .atZone(ZoneId.systemDefault())

                val currentPage = lessons.indexOfFirst { lesson ->
                    val start = lesson.startAt
                        .truncatedTo(ChronoUnit.DAYS)
                        .atZone(ZoneId.systemDefault())

                    ChronoUnit.DAYS.between(now, start) == 0L
                }

                lessons
                    .groupBy(
                        keySelector = { titleFormatter.format(it.startAt) },
                        valueTransform = {
                            it.toUi(rangeFormatter::format)
                        }
                    )
                    .toList() to currentPage
            }
            .onEach { (lessons, currentPage) ->
                _state.update {
                    it.copy(
                        lessons = lessons,
                        pagerState = PagerState(
                            currentPage = currentPage.coerceAtLeast(0),
                            pageCount = { lessons.size }
                        )
                    )
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(screenModelScope)
    }

    private fun deleteLesson(item: LessonItem) {
        ioScope.launch {
            repository.deleteLesson(item.id)
        }
    }

    private fun setViewedItem(item: LessonItem?) {
        _state.update {
            it.copy(viewedItem = item)
        }
    }

    companion object {
        private val titleFormatter by lazy {
            DateTimeFormatter
                .ofPattern("dd.MM")
                .withZone(ZoneId.systemDefault())
                .withLocale(Locale.getDefault())
        }

        private val rangeFormatter by lazy {
            DateTimeFormatter
                .ofPattern("HH:mm")
                .withLocale(Locale.getDefault())
                .withZone(TimeZone.getTimeZone("Asia/Yekaterinburg").toZoneId())
        }
    }
}
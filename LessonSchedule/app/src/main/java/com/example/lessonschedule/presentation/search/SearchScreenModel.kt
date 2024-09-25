package com.example.lessonschedule.presentation.search

import cafe.adriel.voyager.core.model.screenModelScope
import com.example.lessonschedule.DependencyHolder
import com.example.lessonschedule.presentation.base.BaseScreenModel
import com.example.lessonschedule.presentation.models.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.TimeZone

class SearchScreenModel : BaseScreenModel<SearchScreen.Action, SearchScreen.State>(
    initialState = SearchScreen.State()
) {
    private val repository = DependencyHolder.lessonRepository

    override fun onAction(action: SearchScreen.Action) {
        when (action) {
            is SearchScreen.Action.RangePicked -> onDateRangePicked(action.range)
        }
    }

    private fun onDateRangePicked(range: Pair<Long?, Long?>) {
        val (start, end) = range

        if (start == null && end == null) {
            return
        }

        if (start == null) {
            return
        }

        val startInstant = Instant
            .ofEpochMilli(start)
            .minus(5L, ChronoUnit.HOURS)
        val endInstant = end
            ?.let {
                Instant
                    .ofEpochMilli(it)
                    .minus(5L, ChronoUnit.HOURS)
            }
            ?: startInstant

        screenModelScope.launch(Dispatchers.IO) {
            val lessons = repository.getLessonsBetween(
                startAt = startInstant,
                endAt = endInstant
            )
            _state.update {
                it.copy(
                    searchItems = lessons
                        .groupBy(
                            keySelector = { titleFormatter.format(it.startAt) },
                            valueTransform = {
                                it.toUi(rangeFormatter::format)
                            }
                        )
                )
            }
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
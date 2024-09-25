package com.example.lessonschedule.presentation.models

import androidx.compose.runtime.Immutable
import com.example.lessonschedule.domain.Lesson
import com.example.lessonschedule.domain.LessonType
import java.time.Instant

@Immutable
data class LessonItem(
    val id: Int,
    val title: String,
    val startAt: String,
    val endAt: String,
    val type: LessonType,
    val venue: String,
    val teacherName: String
)

inline fun Lesson.toUi(mapTime: (Instant) -> String) = LessonItem(
    id = id,
    title = title,
    type = type,
    venue = venue,
    teacherName = teacherName,
    startAt = mapTime(startAt),
    endAt = mapTime(endAt)
)
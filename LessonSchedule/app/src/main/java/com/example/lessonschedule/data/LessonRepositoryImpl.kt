package com.example.lessonschedule.data

import com.example.lessonschedule.data.database.LessonDao
import com.example.lessonschedule.data.database.LessonEntity
import com.example.lessonschedule.data.database.toDb
import com.example.lessonschedule.data.database.toDomain
import com.example.lessonschedule.domain.Lesson
import com.example.lessonschedule.domain.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.temporal.ChronoUnit

class LessonRepositoryImpl(private val lessonDao: LessonDao): LessonRepository {
    override val lessons: Flow<List<Lesson>> = lessonDao
        .getAllLessons()
        .map { lessons -> lessons.map(LessonEntity::toDomain) }

    override suspend fun createLesson(lesson: Lesson) {
        val lessonEntity = lesson.toDb()
        lessonDao.insertLesson(lessonEntity)
    }

    override suspend fun deleteLesson(id: Int) {
        lessonDao.deleteLesson(id)
    }

    override suspend fun getLessonsBetween(startAt: Instant, endAt: Instant): List<Lesson> =
        lessonDao
            .getLessonsBetween(startAt, endAt.plus(1, ChronoUnit.DAYS))
            .map(LessonEntity::toDomain)
}
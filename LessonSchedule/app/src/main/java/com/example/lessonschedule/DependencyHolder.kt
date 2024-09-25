package com.example.lessonschedule

import android.content.Context
import com.example.lessonschedule.data.LessonRepositoryImpl
import com.example.lessonschedule.data.database.LessonDatabase
import com.example.lessonschedule.domain.LessonRepository

object DependencyHolder {
    private var _lessonRepository: LessonRepositoryImpl? = null
    val lessonRepository: LessonRepository
        get() = requireNotNull(_lessonRepository) {
            "call init first"
        }

    fun init(context: Context) {
        if (_lessonRepository != null) {
            return
        }

        synchronized(this) {
            if (_lessonRepository != null) {
                return
            }

            val database = LessonDatabase.create(context)
            _lessonRepository = LessonRepositoryImpl(database.lessonDao)
        }
    }
}
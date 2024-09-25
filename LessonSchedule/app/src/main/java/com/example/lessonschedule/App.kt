package com.example.lessonschedule

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        DependencyHolder.init(applicationContext)
    }
}
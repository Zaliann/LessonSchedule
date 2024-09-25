package com.example.lessonschedule.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey

abstract class BaseScreen<A, S> : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    final override fun Content() {
        val screenModel = rememberScreenModel { createModel() }
        val state by screenModel.state.collectAsState()
        Content(state = state, onAction = screenModel::onAction)
    }

    @Composable
    abstract fun Content(state: S, onAction: (A) -> Unit)

    abstract fun createModel(): BaseScreenModel<A, S>
}
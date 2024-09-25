package com.example.lessonschedule.presentation

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScreenTransition
import com.example.lessonschedule.presentation.days.schedule.DaysScheduleScreen
import com.example.lessonschedule.presentation.search.SearchScreen
import com.example.lessonschedule.ui.theme.LessonScheduleTheme

@OptIn(ExperimentalVoyagerApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            LessonScheduleTheme {
                Navigator(DaysScheduleScreen()) { navigator ->
                    Column {
                        ReplaceSlideTransition(
                            modifier = Modifier.weight(1f),
                            navigator = navigator
                        )

                        NavigationBar(
                            tonalElevation = 10.dp
                        ) {
                            var selectedItem by rememberSaveable { mutableIntStateOf(0) }

                            titles.fastForEachIndexed { index, title ->
                                val selectedIcon = selectedIcons[index]
                                val unselectedIcon = unselectedIcons[index]

                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = if (selectedItem == index) {
                                                selectedIcon
                                            } else {
                                                unselectedIcon
                                            },
                                            contentDescription = title
                                        )
                                    },
                                    label = { Text(title) },
                                    alwaysShowLabel = false,
                                    selected = selectedItem == index,
                                    onClick = {
                                        selectedItem = index

                                        when (index) {
                                            0 -> navigator.replaceIfNeed(DaysScheduleScreen())
                                            1 -> navigator.replaceIfNeed(SearchScreen())
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ReplaceSlideTransition(
        modifier: Modifier,
        navigator: Navigator
    ) {
        ScreenTransition(
            navigator = navigator,
            modifier = modifier,
            disposeScreenAfterTransitionEnd = true,
            content = { it.Content() },
            transition = {
                val sizeSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                )
                val floatSpec = spring<Float>(
                    stiffness = Spring.StiffnessMediumLow
                )
                when (navigator.lastItem::class) {
                    DaysScheduleScreen::class -> {
                        slideInHorizontally(sizeSpec) { -it } togetherWith
                                slideOutHorizontally(sizeSpec) { it }
                    }
                    SearchScreen::class -> {
                        slideInHorizontally(sizeSpec) { it } togetherWith
                                slideOutHorizontally(sizeSpec) { -it }
                    }
                    else -> {
                        fadeIn(floatSpec) togetherWith fadeOut(floatSpec)
                    }
                }
            }
        )
    }

    private fun Navigator.replaceIfNeed(screen: Screen) {
        val lastItem = lastItemOrNull ?: return
        if (lastItem::class != screen::class) {
            replace(screen)
        }
    }

    companion object {
        private val titles = listOf("Расписание", "Поиск")
        private val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Search)
        private val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Search)
    }
}
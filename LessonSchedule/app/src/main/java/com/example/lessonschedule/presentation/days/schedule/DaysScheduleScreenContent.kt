package com.example.lessonschedule.presentation.days.schedule

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.lessonschedule.R
import com.example.lessonschedule.presentation.common.LessonItemContent
import com.example.lessonschedule.presentation.days.schedule.create.CreateLessonScreen
import com.example.lessonschedule.presentation.days.schedule.dialog.ViewLessonDialog
import com.example.lessonschedule.presentation.models.LessonItem
import kotlinx.coroutines.launch

@Composable
fun DaysScheduleScreenContent(
    state: DaysScheduleScreen.State,
    onAction: (DaysScheduleScreen.Action) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ScreenBody(state, onAction)
        ViewLessonDialog(
            item = state.viewedItem,
            dismiss = {
                onAction(DaysScheduleScreen.Action.CloseViewDialog)
            }
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenBody(
    state: DaysScheduleScreen.State,
    onAction: (DaysScheduleScreen.Action) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Расписание") },
                actions = {
                    val navigator = LocalNavigator.currentOrThrow
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable {
                                navigator.push(CreateLessonScreen())
                            },
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                }
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding())
        val isEmpty = state.lessons.isEmpty()
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = isEmpty,
            label = ""
        ) { isListEmpty ->
            if (isListEmpty) {
                EmptyState(modifier = contentModifier)
            } else {
                Lessons(
                    modifier = contentModifier,
                    pagerState = state.pagerState,
                    lessons = state.lessons,
                    onLessonLick = { onAction(DaysScheduleScreen.Action.OnItemClick(it)) },
                    onDismiss = { onAction(DaysScheduleScreen.Action.DeleteItem(it)) }
                )
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim))

        LottieAnimation(
            composition = composition,
            iterations = Int.MAX_VALUE
        )

        Text(
            text = "Кажется, вы еще не добавили занятий в расписание",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Lessons(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    lessons: List<Pair<String, List<LessonItem>>>,
    onLessonLick: (LessonItem) -> Unit,
    onDismiss: (LessonItem) -> Unit
) {
    Column(modifier = modifier) {
        val selectedTabIndex = pagerState.settledPage
        val scope = rememberCoroutineScope()
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                tabPositions.getOrNull(selectedTabIndex)?.let {
                    TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(it))
                }
            }
        ) {
            lessons.fastForEachIndexed { index, (date, _) ->
                Tab(
                    selected = selectedTabIndex == index,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.outline,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = date) },
                )
            }
        }

        val selectedList = lessons.getOrNull(selectedTabIndex)?.second.orEmpty()
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = rememberLazyListState(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                contentPadding = remember { PaddingValues(vertical = 8.dp) }
            ) {
                items(
                    items = selectedList,
                    key = { it.id }
                ) { item ->
                    LessonItemContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .animateItem(),
                        item = item,
                        onClick = onLessonLick,
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}
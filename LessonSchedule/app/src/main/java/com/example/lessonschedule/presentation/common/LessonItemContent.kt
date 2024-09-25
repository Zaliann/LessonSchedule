package com.example.lessonschedule.presentation.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lessonschedule.domain.LessonType
import com.example.lessonschedule.presentation.models.LessonItem
import com.example.lessonschedule.ui.theme.LessonScheduleTheme

private val timeShape = RoundedCornerShape(4.dp)
private val itemShape = RoundedCornerShape(8.dp)

@Composable
fun LessonItemContent(
    modifier: Modifier = Modifier,
    item: LessonItem,
    onDismiss: ((LessonItem) -> Unit)? = {},
    onClick: (LessonItem) -> Unit = {}
) {
    val leadingLetter = when (item.type) {
        LessonType.LECTURE -> "Lec"
        LessonType.SEMINAR -> "Sem"
        LessonType.LABORATORY -> "Lab"
    }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            when (state) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDismiss?.invoke(item)
                    true
                }
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        enableDismissFromStartToEnd = false,
        gesturesEnabled = onDismiss != null,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = when (dismissState.dismissDirection) {
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                    SwipeToDismissBoxValue.StartToEnd,
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                },
                label = ""
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .clip(itemShape)
                    .background(color = color, shape = itemShape),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null
                )
            }
        },
        content = {
            ListItem(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(itemShape)
                    .clickable { onClick(item) },
                tonalElevation = 1.dp,
                shadowElevation = 8.dp,
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(color = MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = leadingLetter,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Clip
                        )
                    }
                },
                headlineContent = {
                    Text(
                        text = item.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = {
                    Column {
                        Text(
                            text = item.teacherName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = item.venue,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                trailingContent = {
                    Box(
                        modifier = Modifier.height(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = timeShape
                                )
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${item.startAt} - ${item.endAt}",
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                        }
                    }
                }
            )
        }
    )

}

@Composable
@Preview
private fun LessonItemPreview() {
    LessonScheduleTheme(darkTheme = false) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            LessonItemContent(
                item = LessonItem(
                    id = 0,
                    title = "Название предмета",
                    startAt = "5:00",
                    endAt = "8:00",
                    type = LessonType.LECTURE,
                    venue = "Аудитория",
                    teacherName = "Teacher name"
                )
            )
        }
    }
}

@Composable
@Preview
private fun LessonItemPreviewDark() {
    LessonScheduleTheme(darkTheme = true) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            LessonItemContent(
                item = LessonItem(
                    id = 0,
                    title = "Название предмета",
                    startAt = "5:00",
                    endAt = "8:00",
                    type = LessonType.LECTURE,
                    venue = "Аудитория",
                    teacherName = "Teacher name"
                )
            )
        }
    }
}
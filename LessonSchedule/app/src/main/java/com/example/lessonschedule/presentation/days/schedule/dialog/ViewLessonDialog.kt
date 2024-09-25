package com.example.lessonschedule.presentation.days.schedule.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.lessonschedule.presentation.getTitle
import com.example.lessonschedule.presentation.models.LessonItem

@Composable
fun ViewLessonDialog(item: LessonItem?, dismiss: () -> Unit) {
    AnimatedVisibility(
        visible = item != null,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        val lesson = item ?: return@AnimatedVisibility

        Dialog(onDismissRequest = dismiss) {
            val shape = remember { RoundedCornerShape(12.dp) }
            Card(
                shape = shape,
                colors = CardDefaults.elevatedCardColors()
            ) {
                Column(
                    modifier = Modifier.clip(shape),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .clip(shape)
                            .background(color = MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lesson.type.getTitle(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            fontSize = 24.sp
                        )
                    }

                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = lesson.title,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = "${lesson.startAt} - ${lesson.endAt}, ${lesson.venue}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = lesson.teacherName,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, top = 8.dp),
                        onClick = dismiss,
                        content = { Text(text = "Закрыть") }
                    )
                }
            }
        }
    }
}
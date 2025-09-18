package com.ruparts.app.features.task.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.ruparts.app.R
import com.ruparts.app.core.ui.theme.RupartsThemeColors
import com.ruparts.app.core.utils.formatSafely
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import com.ruparts.app.features.taskslist.model.TaskStatus
import com.ruparts.app.features.taskslist.model.TaskType
import com.ruparts.app.features.taskslist.presentation.TimeLeftWarningState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

@Composable
fun TaskScreen(
    task: TaskListItem
) {
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
        ),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Column {
                Main(task)
                Spacer(modifier = Modifier.height(32.dp))
                Attributes()
            }
            BottomButtons(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
private fun Main(task: TaskListItem) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.stack),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "",
            modifier = Modifier.size(24.dp)
        )
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = task.type.displayName,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.clock_image),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "",
            modifier = Modifier.size(24.dp)
        )
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = "Срок:",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyLarge,
        )
        if (task.finishAtDate != null) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = remember(task.finishAtDate) {
                    task.finishAtDate.formatSafely(dateFormatter).orEmpty()
                },
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
            )
            TimeLeftWarning(task.finishAtDate)
        }
    }
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (task.priority) {
            TaskPriority.HIGH ->
                Icon(
                    painter = painterResource(id = R.drawable.arrow_up),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFFFF1644)
                )

            TaskPriority.LOW ->
                Icon(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF1E88E5)
                )

            TaskPriority.MEDIUM ->
                Icon(
                    painter = painterResource(id = R.drawable.equal),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFFFF9800)
                )
        }
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = task.priority.textEquivalent,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.person_image),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "",
            modifier = Modifier.size(24.dp)
        )
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = task.implementer.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.text_image),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "",
            modifier = Modifier.size(24.dp)
        )
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = task.description,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun TimeLeftWarning(finishAtDate: LocalDate) {
    val timeLeftWarningState = remember(finishAtDate) {
        val currentDate = LocalDate.now()
        val daysBetween = ChronoUnit.DAYS.between(currentDate, finishAtDate)
        when {
            daysBetween < 0L -> TimeLeftWarningState("просрочено", true)
            daysBetween == 0L -> TimeLeftWarningState("истекает сегодня", false)
            daysBetween == 1L -> TimeLeftWarningState("осталось 2 дня", false)
            daysBetween == 2L -> TimeLeftWarningState("осталось 3 дня", false)
            else -> null
        }
    }
    if (timeLeftWarningState != null) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .background(
                    color = if (timeLeftWarningState.isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        RupartsThemeColors.Yellow
                    },
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(horizontal = 6.dp, vertical = 2.dp),
            text = timeLeftWarningState.text,
            color = if (timeLeftWarningState.isError) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun Attributes() {
    // for each
    Column {
        Text(
            text = "Аттрибуты",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium
            ),
        )
        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .height(56.dp)
                .fillMaxWidth()
                .background(
                    shape = RoundedCornerShape(CornerSize(8.dp)),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "Доставка покупки",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "1",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.open),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
            )
        }
    }
}

@Composable
private fun BottomButtons(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            Button(
                onClick = { },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp)
                    .padding(top = 8.dp, bottom = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.error90)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "Отклонить",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = { },
                modifier = Modifier
                    .padding(start = 16.dp, end = 20.dp)
                    .padding(top = 8.dp, bottom = 24.dp)
                    .weight(1f)
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "В работу",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview
@Composable
private fun TaskScreenPreview() {
    TaskScreen(
        TaskListItem(
            id = 1,
            status = TaskStatus.TODO,
            priority = TaskPriority.LOW,
            title = "Заголовок задачи",
            description = "Описание задачи",
            implementer = "Кладовщик",
            type = TaskType.CUSTOM,
            createdAtDate = null,
            finishAtDate = null,
            updatedAtDate = null,
        )
    )
}
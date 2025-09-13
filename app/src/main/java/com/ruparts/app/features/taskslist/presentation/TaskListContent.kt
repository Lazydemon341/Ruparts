package com.ruparts.app.features.taskslist.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruparts.app.R
import com.ruparts.app.core.utils.formatSafely
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import com.ruparts.app.features.taskslist.model.TaskStatus
import com.ruparts.app.features.taskslist.model.TaskType
import com.ruparts.app.features.taskslist.presentation.model.TaskListScreenEvent
import java.time.format.DateTimeFormatter

@Composable
fun TaskListContent(
    state: TaskListScreenStateNew,
    onEvent: (TaskListScreenEvent) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
        ),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TaskListTabRow(
                selectedTab = state.selectedTab,
                onTabClick = { tab -> onEvent(TaskListScreenEvent.OnTabClick(tab)) }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TaskList(
                onEvent = onEvent,
                state = state
            )
        }
    }
}

@Composable
fun TaskListTabRow(
    selectedTab: TaskListScreenTab,
    onTabClick: (TaskListScreenTab) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        TaskListTab(
            tabType = TaskListScreenTab.ALL,
            selected = selectedTab == TaskListScreenTab.ALL,
            onTabClick = onTabClick,
        )
        TaskListTab(
            tabType = TaskListScreenTab.IN_WORK,
            selected = selectedTab == TaskListScreenTab.IN_WORK,
            onTabClick = onTabClick,
        )
        TaskListTab(
            tabType = TaskListScreenTab.DONE,
            selected = selectedTab == TaskListScreenTab.DONE,
            onTabClick = onTabClick,
        )
    }
}

@Composable
private fun TaskListTab(
    tabType: TaskListScreenTab,
    selected: Boolean,
    onTabClick: (TaskListScreenTab) -> Unit,
) {
    val textColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Tab(
        selected = selected,
        onClick = { onTabClick(tabType) },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = when (tabType) {
                        TaskListScreenTab.ALL -> "Все"
                        TaskListScreenTab.IN_WORK -> "В работе"
                        TaskListScreenTab.DONE -> "Готово"
                    },
                    color = textColor,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    )
}

@Composable
private fun TaskList(
    onEvent: (TaskListScreenEvent) -> Unit,
    state: TaskListScreenStateNew
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                    items(state.list, key = { it.id }) { item ->
                        // if (item.status == TaskStatus.TODO) {
                            TaskItem(
                                item = item,
                                onEvent = onEvent,
                            )
                }
            }
        }
    }

@Composable
private fun TaskItem(
    item: TaskListItem,
    onEvent: (TaskListScreenEvent) -> Unit,
) {

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically

            ) {
                when (item.priority) {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.width(250.dp)
                    )
                    Text(
                        text = item.status.textEquivalent,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.clock_image),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = item.finishAtDate?.formatSafely(dateFormatter).orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(start = 8.dp)
                        .background(
                            shape = RoundedCornerShape(CornerSize(5.dp)),
                            color = Color(0xFFFFE8A3)
                        ),
                ) {
                    // if (item.finishAtDate != null) {
                    //     val currentDate = LocalDate.now()
                    //     if (item.finishAtDate.isBefore(currentDate)) {
                    //         itemNote.isVisible = true
                    //         itemNote.text = "просрочено"
                    //         itemNote.setBackgroundResource(R.drawable.border_item_note_red)
                    //     } else if (ChronoUnit.DAYS.between(currentDate, item.finishAtDate).toInt() == 0) {
                    //         itemNote.isVisible = true
                    //         itemNote.text = "истекает сегодня"
                    //         itemNote.setBackgroundResource(R.drawable.border_item_note_yellow)
                    //     } else if (ChronoUnit.DAYS.between(currentDate, item.finishAtDate).toInt() == 1) {
                    //         itemNote.isVisible = true
                    //         itemNote.text = "осталось 2 дня"
                    //         itemNote.setBackgroundResource(R.drawable.border_item_note_yellow)
                    //     } else {
                    //         itemNote.isVisible = false
                    //     }
                    // }

                    Text(
                        modifier = Modifier
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        text = "Осталось 3 дня",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .height(20.dp),
                text = item.description,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

}

@Preview
@Composable
private fun TaskListScreenPreview() {
    TaskListContent(
        state = TaskListScreenStateNew(
            selectedTab = TaskListScreenTab.ALL,
            list = listOf(
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
        ),
        onEvent = {}
    )
}

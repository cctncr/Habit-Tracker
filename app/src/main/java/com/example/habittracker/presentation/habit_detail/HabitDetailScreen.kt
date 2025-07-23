package com.example.habittracker.presentation.habit_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.habittracker.presentation.components.ContributionCalendar
import com.example.habittracker.presentation.components.PeriodData
import com.example.habittracker.presentation.home.HabitType
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: Long,
    navController: NavController
) {
    // Mock data
    val habitName = when (habitId) {
        1L -> "Morning Run"
        2L -> "Book"
        3L -> "Water"
        4L -> "Medication"
        else -> "Unknown Habit"
    }
    val habitType = when (habitId) {
        1L, 2L -> HabitType.Numeric("km", 5.0, "Today", "done")
        else -> HabitType.Boolean
    }
    val renewalHours = when (habitId) {
        3L -> 6
        4L -> 12
        else -> 24
    }
    val currentStreak = 15
    val bestStreak = 32
    val totalDays = 87
    val successRate = 78.5f

    // Mock calendar data
    val endDate = LocalDateTime.now()
    val periodsToShow = 365 * (24 / renewalHours) // Show data for ~365 days worth of periods
    val startDate = endDate.minusHours((periodsToShow * renewalHours).toLong())

    val habitData = remember {
        generateMockHabitData(startDate, endDate, renewalHours, habitType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(habitName) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Edit habit */ }) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                    IconButton(onClick = { /* TODO: Delete habit */ }) {
                        Icon(Icons.Default.Delete, "Delete")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Statistics Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Face,
                    iconTint = Color(0xFFFF6B6B),
                    title = "Current Streak",
                    value = "$currentStreak days"
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Star,
                    iconTint = Color(0xFFFFA500),
                    title = "Best Streak",
                    value = "$bestStreak days"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DateRange,
                    iconTint = Color(0xFF4CAF50),
                    title = "Total Days",
                    value = "$totalDays"
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.KeyboardArrowUp,
                    iconTint = Color(0xFF2196F3),
                    title = "Success Rate",
                    value = "${successRate}%"
                )
            }

            // Contribution Calendar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                ContributionCalendar(
                    habitData = habitData,
                    habitType = habitType,
                    renewalHours = renewalHours,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Habit Details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Habit Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    DetailRow(
                        label = "Type",
                        value = when (habitType) {
                            is HabitType.Boolean -> "Yes/No Habit"
                            is HabitType.Numeric -> "Measurable (${habitType.target} ${habitType.unit})"
                        }
                    )

                    DetailRow(
                        label = "Created",
                        value = "3 months ago"
                    )

                    DetailRow(
                        label = "Renewal",
                        value = "Every $renewalHours hours"
                    )

                    if (habitType is HabitType.Numeric) {
                        DetailRow(
                            label = "Average",
                            value = "4.2 ${habitType.unit}/day"
                        )

                        DetailRow(
                            label = "Total",
                            value = "365.4 ${habitType.unit}"
                        )
                    }
                }
            }

            // Recent Activity
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Mock recent activities
                    repeat(5) { index ->
                        RecentActivityItem(
                            date = LocalDate.now().minusDays(index.toLong()),
                            isCompleted = index != 2,
                            value = if (habitType is HabitType.Numeric && index != 2) {
                                "${Random.nextDouble(3.0, 6.0).format(1)} km"
                            } else null
                        )

                        if (index < 4) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RecentActivityItem(
    date: LocalDate,
    isCompleted: Boolean,
    value: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = when (val daysAgo = java.time.Period.between(date, LocalDate.now()).days) {
                    0 -> "Today"
                    1 -> "Yesterday"
                    else -> "$daysAgo days ago"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            value?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isCompleted) Color(0xFF4CAF50) else Color(0xFFE0E0E0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Mock data generator
private fun generateMockHabitData(
    startDate: LocalDateTime,
    endDate: LocalDateTime,
    renewalHours: Int,
    habitType: HabitType
): List<PeriodData> {
    val data = mutableListOf<PeriodData>()
    var currentTime = startDate

    while (currentTime <= endDate) {
        val periodEnd = currentTime.plusHours(renewalHours.toLong())
        val isCompleted = Random.nextFloat() < 0.75f // 75% completion rate

        data.add(
            PeriodData(
                startTime = currentTime,
                endTime = periodEnd,
                isCompleted = isCompleted,
                value = when {
                    !isCompleted -> null
                    habitType is HabitType.Numeric -> Random.nextDouble(2.0, 7.0)
                    else -> null
                },
                target = (habitType as? HabitType.Numeric)?.target
            )
        )

        currentTime = periodEnd
    }

    return data
}

// Extension function for formatting
private fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)
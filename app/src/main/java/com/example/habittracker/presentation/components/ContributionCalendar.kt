package com.example.habittracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habittracker.presentation.home.HabitType
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

data class PeriodData(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val isCompleted: Boolean,
    val value: Double? = null,
    val target: Double? = null
)

@Composable
fun ContributionCalendar(
    habitData: List<PeriodData>,
    habitType: HabitType,
    renewalHours: Int,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(habitData) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    // Group data by days
    val dataByDays = remember(habitData) {
        habitData.groupBy { it.startTime.toLocalDate() }
            .toSortedMap() // Sort by date
    }

    // Extract year and month headers
    val dateHeaders = remember(dataByDays) {
        extractDateHeaders(dataByDays.keys.toList())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        // Title with renewal info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Activity History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Renews every $renewalHours hours",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            // Year headers
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                dateHeaders.years.forEach { (year, dayCount) ->
                    Text(
                        text = year,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width((dayCount * 40).dp),
                        textAlign = TextAlign.Start
                    )
                }
            }

            // Month headers
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                dateHeaders.months.forEach { (month, dayCount) ->
                    Text(
                        text = month,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width((dayCount * 40).dp),
                        textAlign = TextAlign.Start
                    )
                }
            }

            // Days with completion boxes
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Top
            ) {
                dataByDays.forEach { (date, periods) ->
                    Column(
                        modifier = Modifier.width(36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Day number
                        Text(
                            text = date.dayOfMonth.toString(),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )

                        // Divider
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Completion boxes for this day
                        periods.sortedBy { it.startTime }.forEach { period ->
                            PeriodBox(
                                periodData = period,
                                habitType = habitType
                            )
                        }

                        // Fill empty space if fewer periods than max possible in a day
                        val maxPeriodsPerDay = 24 / renewalHours
                        val emptySlots = maxPeriodsPerDay - periods.size
                        repeat(emptySlots) {
                            Spacer(modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (habitType) {
                    is HabitType.Boolean -> "Not done"
                    is HabitType.Numeric -> "No entry"
                },
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp)
            )

            val legendColors = when (habitType) {
                is HabitType.Boolean -> listOf(
                    Color(0xFFEBEDF0), // Not completed
                    Color(0xFF40C463)  // Completed
                )
                is HabitType.Numeric -> listOf(
                    Color(0xFFEBEDF0), // No entry
                    Color(0xFFFFA500), // Below target
                    Color(0xFF40C463)  // Target achieved
                )
            }

            legendColors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .padding(horizontal = 1.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                        .border(
                            0.5.dp,
                            Color(0x1A000000),
                            RoundedCornerShape(2.dp)
                        )
                )
            }

            Text(
                text = when (habitType) {
                    is HabitType.Boolean -> "Done"
                    is HabitType.Numeric -> "Target achieved"
                },
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun PeriodBox(
    periodData: PeriodData,
    habitType: HabitType
) {
    val backgroundColor = when (habitType) {
        is HabitType.Boolean -> {
            if (periodData.isCompleted) Color(0xFF40C463)
            else Color(0xFFEBEDF0)
        }
        is HabitType.Numeric -> {
            when {
                !periodData.isCompleted || periodData.value == null -> Color(0xFFEBEDF0)
                periodData.value >= (periodData.target ?: habitType.target) -> Color(0xFF40C463)
                else -> Color(0xFFFFA500)
            }
        }
    }

    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(backgroundColor)
            .border(
                0.5.dp,
                Color(0x1A000000),
                RoundedCornerShape(2.dp)
            )
    )
}

private data class DateHeaders(
    val years: List<Pair<String, Int>>,
    val months: List<Pair<String, Int>>
)

private fun extractDateHeaders(dates: List<java.time.LocalDate>): DateHeaders {
    if (dates.isEmpty()) return DateHeaders(emptyList(), emptyList())

    val years = mutableListOf<Pair<String, Int>>()
    val months = mutableListOf<Pair<String, Int>>()

    var currentYear = ""
    var currentMonth = ""
    var yearDayCount = 0
    var monthDayCount = 0

    dates.forEach { date ->
        val year = date.year.toString()
        val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())

        // Handle year change
        if (year != currentYear) {
            if (currentYear.isNotEmpty()) {
                years.add(currentYear to yearDayCount)
            }
            currentYear = year
            yearDayCount = 1
        } else {
            yearDayCount++
        }

        // Handle month change
        if (month != currentMonth) {
            if (currentMonth.isNotEmpty()) {
                months.add(currentMonth to monthDayCount)
            }
            currentMonth = month
            monthDayCount = 1
        } else {
            monthDayCount++
        }
    }

    // Add the last year and month
    if (currentYear.isNotEmpty()) {
        years.add(currentYear to yearDayCount)
    }
    if (currentMonth.isNotEmpty()) {
        months.add(currentMonth to monthDayCount)
    }

    return DateHeaders(years, months)
}
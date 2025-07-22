package com.example.habittracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habittracker.presentation.home.HabitType
import com.example.habittracker.presentation.home.HabitUiModel
import kotlin.math.max

@Composable
fun HabitCard(
    habit: HabitUiModel,
    onCompleteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val remainingHours = max(0, habit.renewalHours - habit.lastCompletedHours)
    val timeProgress = habit.lastCompletedHours.toFloat() / habit.renewalHours.toFloat()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(if (habit.type is HabitType.Numeric) 150.dp else 120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (habit.isCompleted)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (habit.isCompleted) 0.dp else 4.dp
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = when {
                            habit.streakCount > 30 -> Color(0xFFFF6B6B)
                            habit.streakCount > 7 -> Color(0xFFFFA500)
                            else -> Color(0xFF95A5A6)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${habit.streakCount} days",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                when (habit.type) {
                    is HabitType.Boolean -> {
                        if (!habit.isCompleted) {
                            LinearProgressIndicator(
                                progress = { timeProgress },
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = when {
                                    remainingHours <= 2 -> Color(0xFFE74C3C)
                                    remainingHours <= 6 -> Color(0xFFF39C12)
                                    else -> MaterialTheme.colorScheme.primary
                                },
                            )
                        }
                    }

                    is HabitType.Numeric -> {
                        NumericProgressIndicator(
                            currentValue = habit.currentValue ?: 0.0,
                            targetValue = habit.type.target,
                            unit = habit.type.unit,
                            prefix = habit.type.prefix,
                            suffix = habit.type.suffix,
                            isCompleted = habit.isCompleted
                        )

                        if (!habit.isCompleted) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$remainingHours hours left",
                                style = MaterialTheme.typography.bodySmall,
                                color = when {
                                    remainingHours <= 2 -> Color(0xFFE74C3C)
                                    remainingHours <= 6 -> Color(0xFFF39C12)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (habit.isCompleted)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    )
                    .clickable(
                        enabled = !habit.isCompleted,
                        onClick = onCompleteClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Complete",
                    tint = if (habit.isCompleted)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun NumericProgressIndicator(
    currentValue: Double,
    targetValue: Double,
    unit: String,
    prefix: String,
    suffix: String,
    isCompleted: Boolean
) {
    val progress = (currentValue / targetValue).coerceIn(0.0, 1.0)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$prefix $currentValue $unit $suffix" +
                    if (isCompleted) "" else " ${targetValue - currentValue} $unit remaining.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isCompleted)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }

    Spacer(modifier = Modifier.height(4.dp))

    LinearProgressIndicator(
        progress = { progress.toFloat() },
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
        color = when {
            isCompleted -> MaterialTheme.colorScheme.primary
            progress >= 0.8f -> Color(0xFF4CAF50)
            progress >= 0.5f -> Color(0xFFFFA500)
            else -> Color(0xFFE74C3C)
        }
    )
}
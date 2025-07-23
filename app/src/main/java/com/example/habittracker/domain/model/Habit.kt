package com.example.habittracker.domain.model

import java.time.LocalDateTime

data class Habit(
    val id: Long = 0,
    val name: String,
    val type: HabitType,
    val renewalHours: Int,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

sealed class HabitType {
    data object Boolean : HabitType()

    data class Numeric(
        val unit: String,
        val target: Double,
        val prefix: String = "",
        val suffix: String = ""
    ) : HabitType()
}
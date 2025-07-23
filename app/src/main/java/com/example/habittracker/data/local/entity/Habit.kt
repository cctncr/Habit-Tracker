package com.example.habittracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: HabitTypeEntity,
    val renewalHours: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

sealed class HabitTypeEntity {
    data object Boolean : HabitTypeEntity()

    data class Numeric(
        val unit: String,
        val target: Double,
        val prefix: String = "",
        val suffix: String = ""
    ) : HabitTypeEntity()
}
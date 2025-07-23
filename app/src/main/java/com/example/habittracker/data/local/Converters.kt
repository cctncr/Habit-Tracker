package com.example.habittracker.data.local

import androidx.room.TypeConverter
import com.example.habittracker.data.local.entity.HabitTypeEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fromHabitType(habitType: HabitTypeEntity): String {
        return when (habitType) {
            is HabitTypeEntity.Boolean -> "BOOLEAN"
            is HabitTypeEntity.Numeric -> "NUMERIC|${habitType.unit}|${habitType.target}|${habitType.prefix}|${habitType.suffix}"
        }
    }

    @TypeConverter
    fun toHabitType(habitTypeString: String): HabitTypeEntity {
        val parts = habitTypeString.split("|")
        return when (parts[0]) {
            "BOOLEAN" -> HabitTypeEntity.Boolean
            "NUMERIC" -> HabitTypeEntity.Numeric(
                unit = parts[1],
                target = parts[2].toDouble(),
                prefix = parts.getOrNull(3) ?: "",
                suffix = parts.getOrNull(4) ?: ""
            )

            else -> throw IllegalArgumentException("Unknown habit type: ${parts[0]}")
        }
    }
}
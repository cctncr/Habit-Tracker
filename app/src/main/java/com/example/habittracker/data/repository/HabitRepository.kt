package com.example.habittracker.data.repository

import com.example.habittracker.data.local.dao.HabitDao
import com.example.habittracker.data.local.entity.HabitEntity
import com.example.habittracker.data.local.entity.HabitTypeEntity
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.model.HabitType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    private val habitDao: HabitDao
) {
    fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getHabitById(habitId: Long): Habit? {
        return habitDao.getHabitById(habitId)?.toDomainModel()
    }

    suspend fun insertHabit(habit: Habit): Long {
        return habitDao.insertHabit(habit.toEntity())
    }

    suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit.toEntity())
    }

    suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabitById(habitId)
    }
}

private fun HabitEntity.toDomainModel(): Habit {
    return Habit(
        id = id,
        name = name,
        type = when (type) {
            is HabitTypeEntity.Boolean -> HabitType.Boolean
            is HabitTypeEntity.Numeric -> HabitType.Numeric(
                unit = type.unit,
                target = type.target,
                prefix = type.prefix,
                suffix = type.suffix
            )
        },
        renewalHours = renewalHours,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        name = name,
        type = when (type) {
            is HabitType.Boolean -> HabitTypeEntity.Boolean
            is HabitType.Numeric -> HabitTypeEntity.Numeric(
                unit = type.unit,
                target = type.target,
                prefix = type.prefix,
                suffix = type.suffix
            )
        },
        renewalHours = renewalHours,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
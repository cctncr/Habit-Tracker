package com.example.habittracker.presentation.add_habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.repository.HabitRepository
import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.model.HabitType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddHabitUiState(
    val habitName: String = "",
    val habitType: HabitTypeSelection = HabitTypeSelection.BOOLEAN,
    val renewalPeriod: String = "24",
    val renewalUnit: RenewalUnit = RenewalUnit.HOURS,
    val targetValue: String = "",
    val unit: String = "",
    val prefix: String = "",
    val suffix: String = "",
    val isLoading: Boolean = false,
    val isSaveEnabled: Boolean = false
)

enum class HabitTypeSelection {
    BOOLEAN, NUMERIC
}

enum class RenewalUnit(val displayName: String, val toHours: Int) {
    HOURS("Hours", 1),
    DAYS("Days", 24)
}

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddHabitUiState())
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()

    fun updateHabitName(name: String) {
        _uiState.value = _uiState.value.copy(habitName = name)
        updateSaveButtonState()
    }

    fun updateHabitType(type: HabitTypeSelection) {
        _uiState.value = _uiState.value.copy(habitType = type)
        updateSaveButtonState()
    }

    fun updateRenewalPeriod(period: String) {
        _uiState.value = _uiState.value.copy(renewalPeriod = period)
        updateSaveButtonState()
    }

    fun updateRenewalUnit(unit: RenewalUnit) {
        _uiState.value = _uiState.value.copy(renewalUnit = unit)
    }

    fun updateTargetValue(value: String) {
        _uiState.value = _uiState.value.copy(targetValue = value)
        updateSaveButtonState()
    }

    fun updateUnit(unit: String) {
        _uiState.value = _uiState.value.copy(unit = unit)
        updateSaveButtonState()
    }

    fun updatePrefix(prefix: String) {
        _uiState.value = _uiState.value.copy(prefix = prefix)
    }

    fun updateSuffix(suffix: String) {
        _uiState.value = _uiState.value.copy(suffix = suffix)
    }

    private fun updateSaveButtonState() {
        val state = _uiState.value
        val isValid = state.habitName.isNotBlank() &&
                state.renewalPeriod.isNotBlank() &&
                state.renewalPeriod.toIntOrNull() != null &&
                (state.habitType == HabitTypeSelection.BOOLEAN ||
                        (state.targetValue.isNotBlank() &&
                                state.targetValue.toDoubleOrNull() != null &&
                                state.unit.isNotBlank()))

        _uiState.value = state.copy(isSaveEnabled = isValid)
    }

    fun saveHabit(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.isSaveEnabled) return

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)

            try {
                val habitType = when (state.habitType) {
                    HabitTypeSelection.BOOLEAN -> HabitType.Boolean
                    HabitTypeSelection.NUMERIC -> HabitType.Numeric(
                        unit = state.unit,
                        target = state.targetValue.toDouble(),
                        prefix = state.prefix,
                        suffix = state.suffix
                    )
                }

                val renewalHours = state.renewalPeriod.toInt() * state.renewalUnit.toHours

                val habit = Habit(
                    name = state.habitName,
                    type = habitType,
                    renewalHours = renewalHours
                )

                repository.insertHabit(habit)
                onSuccess()
            } catch (e: Exception) {
                // TODO: Handle error
                e.printStackTrace()
            } finally {
                _uiState.value = state.copy(isLoading = false)
            }
        }
    }
}
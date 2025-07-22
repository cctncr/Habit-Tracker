package com.example.habittracker.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.habittracker.presentation.components.BottomNavigationBar
import com.example.habittracker.presentation.components.HabitCard
import com.example.habittracker.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // For testing
    val mockHabits = remember {
        listOf(
            HabitUiModel(
                id = 1,
                name = "Run",
                streakCount = 15,
                renewalHours = 24,
                lastCompletedHours = 18,
                isCompleted = false,
                type = HabitType.Numeric("km", 2.0, "Today", "done."),
            ),
            HabitUiModel(
                id = 2,
                name = "Book",
                streakCount = 7,
                renewalHours = 24,
                lastCompletedHours = 5,
                isCompleted = true,
                type = HabitType.Numeric("page", 100.0, "Today", "done."),
                currentValue = 100.0
            ),
            HabitUiModel(
                id = 3,
                name = "Water",
                streakCount = 30,
                renewalHours = 6,
                lastCompletedHours = 2,
                isCompleted = false,
                type = HabitType.Boolean
            )
        )
//        emptyList<HabitUiModel>()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Habit Streak",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddHabit.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        if (mockHabits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🎯",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ur habits are empty",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Create new habits",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockHabits) { habit ->
                    HabitCard(
                        habit = habit,
                        onCompleteClick = {
                            // TODO: Handle completion
                        },
                        onClick = {
                            // TODO: Navigate to detail
                        }
                    )
                }
            }
        }
    }
}

sealed class HabitType {
    data object Boolean : HabitType()
    data class Numeric(
        val unit: String,
        val target: Double,
        val prefix: String = "",
        val suffix: String = ""
    ) : HabitType()
}

data class HabitUiModel(
    val id: Long,
    val name: String,
    val type: HabitType,
    val streakCount: Int,
    val renewalHours: Int,
    val lastCompletedHours: Int,
    val isCompleted: Boolean,
    val currentValue: Double? = null
)
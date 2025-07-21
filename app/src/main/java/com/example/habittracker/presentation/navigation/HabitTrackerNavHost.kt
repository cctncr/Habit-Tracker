package com.example.habittracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habittracker.presentation.add_habit.AddHabitScreen
import com.example.habittracker.presentation.home.HomeScreen
import com.example.habittracker.presentation.settings.SettingsScreen
import com.example.habittracker.presentation.statistics.StatisticsScreen

@Composable
fun HabitTrackerNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(route = Screen.AddHabit.route) {
            AddHabitScreen(navController = navController)
        }

        composable(route = Screen.Statistics.route) {
            StatisticsScreen(navController = navController)
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddHabit : Screen("add_habit")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")
}
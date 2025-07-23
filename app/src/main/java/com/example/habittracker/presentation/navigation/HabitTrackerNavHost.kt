package com.example.habittracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habittracker.presentation.add_habit.AddHabitScreen
import com.example.habittracker.presentation.habit_detail.HabitDetailScreen
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

        composable(
            route = Screen.HabitDetail.route,
            arguments = listOf(
                navArgument("habitId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: 0L
            HabitDetailScreen(
                habitId = habitId,
                navController = navController
            )
        }
    }
}

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddHabit : Screen("add_habit")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")
    data object HabitDetail : Screen("habit_detail/{habitId}") {
        fun createRoute(habitId: Long) = "habit_detail/$habitId"
    }
}
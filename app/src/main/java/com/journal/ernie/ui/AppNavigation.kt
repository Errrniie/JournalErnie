package com.journal.ernie.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.journal.ernie.Screen

@Composable
fun AppNavigation(
    currentScreen: Screen,
    onNavigateTo: (Screen) -> Unit
) {
    when (currentScreen) {
        is Screen.Home -> {
            // Placeholder - HomeScreen will be created in Phase 2
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Home Screen")
            }
        }
        is Screen.WorkoutList -> {
            // Placeholder - WorkoutListScreen will be created in Phase 4
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Workout List Screen")
            }
        }
        is Screen.WorkoutSession -> {
            // Placeholder - WorkoutSessionScreen will be created in Phase 5
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Workout Session Screen")
            }
        }
    }
}

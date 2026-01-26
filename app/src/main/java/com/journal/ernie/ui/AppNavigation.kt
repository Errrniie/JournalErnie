package com.journal.ernie.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.journal.ernie.Screen
import com.journal.ernie.viewmodel.WorkoutViewModel

@Composable
fun AppNavigation(
    currentScreen: Screen,
    onNavigateTo: (Screen) -> Unit,
    viewModel: WorkoutViewModel
) {
    when (currentScreen) {
        is Screen.Home -> {
            HomeScreen(onNavigateTo = onNavigateTo)
        }
        is Screen.WorkoutList -> {
            WorkoutListScreen(
                viewModel = viewModel,
                onNavigateTo = onNavigateTo,
                onNavigateBack = { onNavigateTo(Screen.Home) }
            )
        }
        is Screen.WorkoutSession -> {
            WorkoutSessionScreen(
                viewModel = viewModel,
                onNavigateBack = { onNavigateTo(Screen.WorkoutList) }
            )
        }
    }
}

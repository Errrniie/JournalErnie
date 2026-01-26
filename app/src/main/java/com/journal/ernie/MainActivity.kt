package com.journal.ernie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.journal.ernie.ui.AppNavigation
import com.journal.ernie.viewmodel.WorkoutViewModel

sealed class Screen {
    object Home : Screen()
    object WorkoutList : Screen()
    object WorkoutSession : Screen()
}

class MainActivity : ComponentActivity() {
    private val workoutViewModel: WorkoutViewModel by lazy {
        ViewModelProvider(this)[WorkoutViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            // Navigation state - remembers current screen across recompositions

            var currentScreen: Screen by remember { mutableStateOf(Screen.Home)}

            // Navigation callback - updates screen when called
            val onNavigateTo: (Screen) -> Unit = { screen ->
                currentScreen = screen
            }
            
            // Material3 theme wrapper
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Root navigation composable
                    AppNavigation(
                        currentScreen = currentScreen,
                        onNavigateTo = onNavigateTo,
                        viewModel = workoutViewModel
                    )
                }
            }
        }
    }
}
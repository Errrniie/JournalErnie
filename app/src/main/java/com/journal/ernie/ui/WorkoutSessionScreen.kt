package com.journal.ernie.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.journal.ernie.ui.components.MuscleGroupCard
import com.journal.ernie.ui.components.TimerDisplay
import com.journal.ernie.ui.dialogs.AddExerciseDialog
import com.journal.ernie.ui.dialogs.AddMuscleGroupDialog
import com.journal.ernie.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(
    viewModel: WorkoutViewModel,
    onNavigateBack: () -> Unit
) {
    // Collect state from ViewModel
    val currentSession by viewModel.currentSession.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    
    // Dialog states
    var showAddMuscleGroupDialog by remember { mutableStateOf(false) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var selectedMuscleGroupId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentSession?.name ?: "Workout Session"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMuscleGroupDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Muscle Group"
                )
            }
        }
    ) { paddingValues ->
        val session = currentSession // Store in local variable for smart cast
        if (session == null) {
            // No active session
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "No active workout session",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Please select a session from the list",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            // Display session content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Timer display
                item {
                    TimerDisplay(
                        elapsedTimeSeconds = timerState.elapsedTimeSeconds,
                        isRunning = timerState.isRunning,
                        onStart = { viewModel.startTimer() },
                        onPause = { viewModel.pauseTimer() },
                        onReset = { viewModel.resetTimer() }
                    )
                }

                // Muscle groups
                if (session.muscleGroups.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FitnessCenter,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No muscle groups yet",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap the + button to add a muscle group",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(session.muscleGroups) { muscleGroup ->
                        MuscleGroupCard(
                            muscleGroup = muscleGroup,
                            onAddExercise = {
                                selectedMuscleGroupId = muscleGroup.id
                                showAddExerciseDialog = true
                            },
                            onRemoveGroup = {
                                viewModel.removeMuscleGroup(muscleGroup.id)
                            },
                            onAddSet = { exerciseId ->
                                viewModel.addSet(muscleGroup.id, exerciseId)
                            },
                            onRemoveExercise = { exerciseId ->
                                viewModel.removeExercise(muscleGroup.id, exerciseId)
                            },
                            onUpdateSet = { exerciseId, setIndex, reps, weight, comment ->
                                viewModel.updateSet(muscleGroup.id, exerciseId, setIndex, reps, weight, comment)
                            },
                            onRemoveSet = { exerciseId, setIndex ->
                                viewModel.removeSet(muscleGroup.id, exerciseId, setIndex)
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Add muscle group dialog
    if (showAddMuscleGroupDialog) {
        AddMuscleGroupDialog(
            onDismiss = { showAddMuscleGroupDialog = false },
            onConfirm = { muscleGroupName ->
                viewModel.addMuscleGroup(muscleGroupName)
                showAddMuscleGroupDialog = false
            }
        )
    }
    
    // Add exercise dialog
    if (showAddExerciseDialog && selectedMuscleGroupId != null) {
        AddExerciseDialog(
            onDismiss = { 
                showAddExerciseDialog = false
                selectedMuscleGroupId = null
            },
            onConfirm = { exerciseName ->
                selectedMuscleGroupId?.let { groupId ->
                    viewModel.addExercise(groupId, exerciseName)
                }
                showAddExerciseDialog = false
                selectedMuscleGroupId = null
            }
        )
    }
}
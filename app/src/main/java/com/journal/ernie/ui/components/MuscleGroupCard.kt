package com.journal.ernie.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.journal.ernie.data.MuscleGroup

@Composable
fun MuscleGroupCard(
    muscleGroup: MuscleGroup,
    onAddExercise: () -> Unit,
    onRemoveGroup: () -> Unit,
    onAddSet: (String) -> Unit,  // exerciseId
    onRemoveExercise: (String) -> Unit,  // exerciseId
    onUpdateSet: (String, Int, Int, Float, String?) -> Unit,  // exerciseId, setIndex, reps, weight, comment
    onRemoveSet: (String, Int) -> Unit,  // exerciseId, setIndex
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row: Name and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = muscleGroup.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onRemoveGroup
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Remove Muscle Group",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // Exercises list
            if (muscleGroup.exercises.isEmpty()) {
                Text(
                    text = "No exercises yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    muscleGroup.exercises.forEach { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onAddSet = { onAddSet(exercise.id) },
                            onRemoveExercise = { onRemoveExercise(exercise.id) },
                            onUpdateSet = { setIndex, reps, weight, comment ->
                                onUpdateSet(exercise.id, setIndex, reps, weight, comment)
                            },
                            onRemoveSet = { setIndex ->
                                onRemoveSet(exercise.id, setIndex)
                            }
                        )
                    }
                }
            }
            
            // Add exercise button
            OutlinedButton(
                onClick = onAddExercise,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Exercise"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Exercise")
            }
        }
    }
}

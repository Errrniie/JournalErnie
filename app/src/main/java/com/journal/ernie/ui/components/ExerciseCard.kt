package com.journal.ernie.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.spacedBy
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
import com.journal.ernie.data.Exercise

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onAddSet: () -> Unit,
    onRemoveExercise: () -> Unit,
    onUpdateSet: (Int, Int, Float, String?) -> Unit,  // setIndex, reps, weight, comment
    onRemoveSet: (Int) -> Unit,  // setIndex
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header row: Name and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onRemoveExercise
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Remove Exercise",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // Sets list
            if (exercise.sets.isEmpty()) {
                Text(
                    text = "No sets yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    exercise.sets.forEachIndexed { index, set ->
                        SetRow(
                            set = set,
                            setNumber = index + 1,
                            onUpdate = { reps, weight, comment ->
                                onUpdateSet(index, reps, weight, comment)
                            },
                            onDelete = {
                                onRemoveSet(index)
                            }
                        )
                    }
                }
            }
            
            // Add set button
            OutlinedButton(
                onClick = onAddSet,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Set"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Set")
            }
        }
    }
}

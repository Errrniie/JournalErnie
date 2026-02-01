package com.journal.ernie.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.journal.ernie.data.SetEntry

@Composable
fun SetRow(
    set: SetEntry,
    setNumber: Int,
    onUpdate: (Int, Float, String?) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var repsText by remember { mutableStateOf(set.reps.toString()) }
    var weightText by remember { mutableStateOf(set.weight.toString()) }
    var commentText by remember { mutableStateOf(set.comment ?: "") }
    var repsError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        )
    ) {
        if (isEditing) {
            // Edit mode
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header with set number and buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Set $setNumber",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        TextButton(onClick = {
                            // Save
                            val reps = repsText.toIntOrNull() ?: 0
                            val weight = weightText.toFloatOrNull() ?: 0f
                            // Validate before saving
                            if (reps >= 0 && reps <= 1000 && weight >= 0f && weight <= 1000f) {
                                onUpdate(reps, weight, commentText.ifEmpty { null })
                                isEditing = false
                            }
                        }) {
                            Text("Save")
                        }
                        TextButton(onClick = {
                            // Cancel - reset to original values
                            repsText = set.reps.toString()
                            weightText = set.weight.toString()
                            commentText = set.comment ?: ""
                            repsError = null
                            weightError = null
                            isEditing = false
                        }) {
                            Text("Cancel")
                        }
                        IconButton(onClick = {
                            onDelete()
                            isEditing = false
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Set",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                
                // Text fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = repsText,
                        onValueChange = { 
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                val value = it.toIntOrNull() ?: 0
                                if (value <= 1000) {
                                    repsText = it
                                    repsError = null
                                } else {
                                    repsError = "Maximum 1000 reps"
                                }
                            }
                        },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = repsError != null,
                        supportingText = { repsError?.let { Text(it) } }
                    )
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { 
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                val value = it.toFloatOrNull() ?: 0f
                                if (value <= 1000f) {
                                    weightText = it
                                    weightError = null
                                } else {
                                    weightError = "Maximum 1000kg"
                                }
                            }
                        },
                        label = { Text("Weight") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = weightError != null,
                        supportingText = { weightError?.let { Text(it) } }
                    )
                }
                
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Comment (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 2
                )
            }
        } else {
            // Display mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Set $setNumber",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${set.reps} reps × ${set.weight}kg",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    val comment = set.comment
                    if (comment != null && comment.isNotEmpty()) {
                        Text(
                            text = comment,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                Row {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Set"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Set",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

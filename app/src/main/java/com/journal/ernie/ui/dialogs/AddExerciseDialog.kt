package com.journal.ernie.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var exerciseName by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val isValid = remember(exerciseName) {
        val trimmed = exerciseName.trim()
        trimmed.isNotEmpty() && trimmed.length <= 50
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = "Add Exercise",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Text field
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { 
                        exerciseName = it
                        errorMessage = when {
                            it.trim().isEmpty() -> null // Don't show error while typing
                            it.trim().length > 50 -> "Name must be 50 characters or less"
                            else -> null
                        }
                    },
                    label = { Text("Exercise Name") },
                    placeholder = { Text("e.g., Bench Press, Squats") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = {
                        val msg = errorMessage
                        if (msg != null) {
                            Text(msg)
                        }
                    }
                )
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val trimmedName = exerciseName.trim()
                            if (trimmedName.isNotEmpty() && trimmedName.length <= 50) {
                                onConfirm(trimmedName)
                                onDismiss()
                            }
                        },
                        enabled = isValid
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

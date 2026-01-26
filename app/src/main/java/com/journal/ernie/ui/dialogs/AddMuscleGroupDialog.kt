package com.journal.ernie.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.journal.ernie.data.MuscleGroupPresets

@Composable
fun AddMuscleGroupDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var muscleGroupName by rememberSaveable { mutableStateOf("") }
    var showPresetDropdown by remember { mutableStateOf(false) }
    
    // Get preset suggestions based on input
    val presetSuggestions = remember(muscleGroupName) {
        if (muscleGroupName.isBlank()) {
            MuscleGroupPresets.getAllPresets()
        } else {
            MuscleGroupPresets.getPresetSuggestions(muscleGroupName)
        }
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
                    text = "Add Muscle Group",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Text field with dropdown
                Column {
                    OutlinedTextField(
                        value = muscleGroupName,
                        onValueChange = { 
                            muscleGroupName = it
                            showPresetDropdown = it.isNotBlank() && presetSuggestions.isNotEmpty()
                        },
                        label = { Text("Muscle Group Name") },
                        placeholder = { Text("e.g., Chest, Back, Legs") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Preset suggestions dropdown
                    if (showPresetDropdown && presetSuggestions.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                items(presetSuggestions) { preset ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                muscleGroupName = preset
                                                showPresetDropdown = false
                                            }
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = preset,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
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
                            val trimmedName = muscleGroupName.trim()
                            if (trimmedName.isNotEmpty()) {
                                onConfirm(trimmedName)
                                onDismiss()
                            }
                        },
                        enabled = muscleGroupName.trim().isNotEmpty()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

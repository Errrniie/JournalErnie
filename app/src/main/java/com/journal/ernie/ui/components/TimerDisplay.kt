package com.journal.ernie.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun TimerDisplay(
    elapsedTimeSeconds: Long,
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Local state for smooth animation
    var displayTime by remember { mutableStateOf(elapsedTimeSeconds) }
    
    // Update display time smoothly when running
    LaunchedEffect(isRunning, elapsedTimeSeconds) {
        if (isRunning) {
            // Smooth increment every second
            while (true) {
                delay(1000)
                displayTime = elapsedTimeSeconds
            }
        } else {
            // Sync with actual time when paused
            displayTime = elapsedTimeSeconds
        }
    }
    
    // Format time display
    val hours = displayTime / 3600
    val minutes = (displayTime % 3600) / 60
    val seconds = displayTime % 60
    
    val timeString = if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Time display
            Text(
                text = timeString,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Start button (only when paused)
                if (!isRunning) {
                    Button(
                        onClick = onStart,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Start",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Start")
                    }
                } else {
                    // Pause button (only when running)
                    Button(
                        onClick = onPause,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Pause,
                            contentDescription = "Pause",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Pause")
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Reset button (always visible)
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reset",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Reset")
                }
            }
        }
    }
}

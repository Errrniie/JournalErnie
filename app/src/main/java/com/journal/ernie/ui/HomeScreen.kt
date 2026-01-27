package com.journal.ernie.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.journal.ernie.Screen

@Composable
fun HomeScreen(
    onNavigateTo: (Screen) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // First row: 2 placeholder boxes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NavigationBox(
                title = "Placeholder 1",
                subtitle = "Coming Soon",
                enabled = false,
                onClick = { },
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )
            NavigationBox(
                title = "Placeholder 2",
                subtitle = "Coming Soon",
                enabled = false,
                onClick = { },
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )
        }
        
        // Second row: Center box (Workout)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            NavigationBox(
                title = "Workout",
                subtitle = "Track your workouts",
                enabled = true,
                onClick = { onNavigateTo(Screen.WorkoutList) },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(180.dp)
            )
        }
        
        // Third row: 2 placeholder boxes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NavigationBox(
                title = "Placeholder 3",
                subtitle = "Coming Soon",
                enabled = false,
                onClick = { },
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )
            NavigationBox(
                title = "Placeholder 4",
                subtitle = "Coming Soon",
                enabled = false,
                onClick = { },
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            )
        }
        }
    }
}

@Composable
fun NavigationBox(
    title: String,
    subtitle: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (enabled) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    }
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = if (enabled) {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        }
                    )
                }
            }
        }
    }
}

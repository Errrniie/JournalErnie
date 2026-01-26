# Phase 4: Workout List Screen - Detailed Implementation Guide

## Overview

This document provides step-by-step instructions for implementing Phase 4 of the Workout Tracking App. Phase 4 creates the WorkoutListScreen that displays all workout sessions, allows creating new sessions via a dialog, and navigates to the workout session screen when a session is selected.

**Goal:** Create a functional workout list screen that displays all sessions, allows creating new sessions, and integrates with the ViewModel from Phase 3. This screen serves as the entry point to the workout section.

**Prerequisites:** 
- Phase 1 must be completed (all data models exist)
- Phase 2 must be completed (HomeScreen and navigation exist)
- Phase 3 must be completed (WorkoutViewModel exists)
- Understanding of Compose StateFlow collection
- Basic knowledge of Material3 components (LazyColumn, Card, FAB, Dialog)

---

## Phase 4 Overview

Phase 4 consists of 4 main tasks:
1. Update MainActivity to initialize and provide ViewModel
2. Create CreateSessionDialog for creating new workout sessions
3. Create WorkoutSessionCard component for displaying session information
4. Create WorkoutListScreen and integrate with ViewModel and navigation

**Estimated Time:** 2-3 hours
**Files to Create:** 3 new files
**Files to Modify:** 2 existing files

---

## Task 4.1: Update MainActivity to Initialize ViewModel

**File Path:** `src/main/java/com/journal/ernie/MainActivity.kt`

**Purpose:** Initialize the WorkoutViewModel and pass it to AppNavigation so it can be used by screens.

### Step-by-Step Instructions:

1. **Open MainActivity.kt:**
   - File should already exist from Phase 1
   - Current content has navigation setup but no ViewModel

2. **Add ViewModel import:**
   - Add to imports: `import androidx.lifecycle.viewmodel.compose.viewModel`
   - This provides the `viewModel()` function for Compose

3. **Add WorkoutViewModel import:**
   - Add to imports: `import com.journal.ernie.viewmodel.WorkoutViewModel`
   - This imports the ViewModel we created in Phase 3

4. **Initialize ViewModel in setContent:**
   - Inside `setContent { }` block, before MaterialTheme
   - Add: `val workoutViewModel: WorkoutViewModel = viewModel()`
   - This creates a ViewModel instance scoped to the activity

5. **Pass ViewModel to AppNavigation:**
   - Update AppNavigation call to include ViewModel parameter
   - Change: `AppNavigation(currentScreen = currentScreen, onNavigateTo = onNavigateTo)`
   - To: `AppNavigation(currentScreen = currentScreen, onNavigateTo = onNavigateTo, viewModel = workoutViewModel)`

6. **Update AppNavigation signature (will be done in Task 4.4):**
   - For now, just note that AppNavigation will need to accept ViewModel parameter
   - We'll update AppNavigation in Task 4.4

### Expected Code Structure (MainActivity.kt after update):

```kotlin
package com.journal.ernie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.journal.ernie.ui.AppNavigation
import com.journal.ernie.viewmodel.WorkoutViewModel

sealed class Screen {
    object Home : Screen()
    object WorkoutList : Screen()
    object WorkoutSession : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            // Navigation state
            var currentScreen by remember { mutableStateOf(Screen.Home) }
            
            // Navigation callback
            val onNavigateTo: (Screen) -> Unit = { screen ->
                currentScreen = screen
            }
            
            // Initialize ViewModel
            val workoutViewModel: WorkoutViewModel = viewModel()
            
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
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
```

### Important Notes:

- **Why viewModel()?** This is the Compose way to get a ViewModel. It creates a ViewModel scoped to the activity lifecycle.
- **Why in setContent?** ViewModel initialization needs to happen in the Compose context. `viewModel()` is a composable function.
- **Why pass to AppNavigation?** AppNavigation will distribute the ViewModel to child screens that need it.
- **ViewModel lifecycle:** The ViewModel will survive configuration changes and be shared across all screens.

### Testing Checklist:

After updating MainActivity, verify:
- [ ] File compiles without errors
- [ ] ViewModel import resolves correctly
- [ ] viewModel() function is accessible
- [ ] App still runs without crashing
- [ ] ViewModel is initialized (check logcat if needed)

---

## Task 4.2: Create CreateSessionDialog

**File Path:** `src/main/java/com/journal/ernie/ui/dialogs/CreateSessionDialog.kt`

**Purpose:** Create a dialog that prompts the user to enter a name for a new workout session.

### Step-by-Step Instructions:

1. **Create the dialogs package directory (if not exists):**
   - Right-click on `ui` package folder
   - Select "New" → "Package"
   - Name: `dialogs`

2. **Create the file:**
   - Right-click on `dialogs` package folder
   - Select "New" → "Kotlin Class/File"
   - Name: `CreateSessionDialog`
   - Type: "File"

3. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.ui.dialogs
   ```

4. **Add all required imports:**
   ```kotlin
   import androidx.compose.foundation.layout.Arrangement
   import androidx.compose.foundation.layout.Column
   import androidx.compose.foundation.layout.Row
   import androidx.compose.foundation.layout.Spacer
   import androidx.compose.foundation.layout.fillMaxWidth
   import androidx.compose.foundation.layout.height
   import androidx.compose.foundation.layout.padding
   import androidx.compose.foundation.layout.width
   import androidx.compose.material3.Button
   import androidx.compose.material3.OutlinedButton
   import androidx.compose.material3.OutlinedTextField
   import androidx.compose.material3.Text
   import androidx.compose.material3.TextButton
   import androidx.compose.runtime.Composable
   import androidx.compose.runtime.getValue
   import androidx.compose.runtime.mutableStateOf
   import androidx.compose.runtime.remember
   import androidx.compose.runtime.saveable.rememberSaveable
   import androidx.compose.runtime.setValue
   import androidx.compose.ui.Modifier
   import androidx.compose.ui.unit.dp
   import androidx.compose.ui.window.Dialog
   ```

5. **Create the composable function:**
   - Function signature:
   ```kotlin
   @Composable
   fun CreateSessionDialog(
       onDismiss: () -> Unit,
       onConfirm: (String) -> Unit
   )
   ```
   - Parameters:
     - `onDismiss: () -> Unit` - Called when user cancels or dismisses dialog
     - `onConfirm: (String) -> Unit` - Called when user confirms, passes session name

6. **Add state for text field:**
   - Inside function: `var sessionName by rememberSaveable { mutableStateOf("") }`
   - Use `rememberSaveable` to preserve text on configuration changes
   - This holds the text the user types

7. **Create Dialog composable:**
   - Use `Dialog(onDismissRequest = onDismiss) { }`
   - Inside Dialog, create a Card or Surface for the dialog content

8. **Add dialog content:**
   - Column layout with:
     - Title: "Create New Workout Session"
     - TextField for session name
     - Row with Cancel and Create buttons
   - Use Material3 components for styling

9. **Add validation:**
   - Disable Create button if sessionName is blank
   - Trim whitespace from session name before confirming

10. **Handle button clicks:**
    - Cancel button: Call `onDismiss()`
    - Create button: Call `onConfirm(sessionName.trim())` then `onDismiss()`

### Expected Code Structure:

```kotlin
package com.journal.ernie.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var sessionName by rememberSaveable { mutableStateOf("") }
    
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
                    text = "Create New Workout Session",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Text field
                OutlinedTextField(
                    value = sessionName,
                    onValueChange = { sessionName = it },
                    label = { Text("Session Name") },
                    placeholder = { Text("e.g., Morning Workout, Leg Day") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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
                            val trimmedName = sessionName.trim()
                            if (trimmedName.isNotEmpty()) {
                                onConfirm(trimmedName)
                                onDismiss()
                            }
                        },
                        enabled = sessionName.trim().isNotEmpty()
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}
```

### Important Notes:

- **Why rememberSaveable?** Preserves text input when screen rotates. Better UX than losing what user typed.
- **Why trim()?** Removes leading/trailing whitespace. Prevents sessions with names like "  Workout  ".
- **Why enabled check?** Prevents creating sessions with empty names. Better validation.
- **Why Card inside Dialog?** Provides Material3 styling and elevation. Makes dialog look polished.
- **Why singleLine?** Session names should be single line. Prevents multiline input.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] Dialog displays when called
- [ ] Text field accepts input
- [ ] Create button is disabled when text is empty
- [ ] Create button is enabled when text is entered
- [ ] Cancel button dismisses dialog
- [ ] Create button calls onConfirm with trimmed name
- [ ] Dialog dismisses after Create is clicked

---

## Task 4.3: Create WorkoutSessionCard Component

**File Path:** `src/main/java/com/journal/ernie/ui/components/WorkoutSessionCard.kt`

**Purpose:** Create a reusable card component that displays workout session information (name, date, summary).

### Step-by-Step Instructions:

1. **Create the components package directory (if not exists):**
   - Right-click on `ui` package folder
   - Select "New" → "Package"
   - Name: `components`

2. **Create the file:**
   - Right-click on `components` package folder
   - Select "New" → "Kotlin Class/File"
   - Name: `WorkoutSessionCard`
   - Type: "File"

3. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.ui.components
   ```

4. **Add all required imports:**
   ```kotlin
   import androidx.compose.foundation.clickable
   import androidx.compose.foundation.layout.Column
   import androidx.compose.foundation.layout.Row
   import androidx.compose.foundation.layout.Spacer
   import androidx.compose.foundation.layout.fillMaxWidth
   import androidx.compose.foundation.layout.height
   import androidx.compose.foundation.layout.padding
   import androidx.compose.foundation.layout.width
   import androidx.compose.material3.Card
   import androidx.compose.material3.CardDefaults
   import androidx.compose.material3.MaterialTheme
   import androidx.compose.material3.Text
   import androidx.compose.runtime.Composable
   import androidx.compose.ui.Alignment
   import androidx.compose.ui.Modifier
   import androidx.compose.ui.text.font.FontWeight
   import androidx.compose.ui.unit.dp
   import com.journal.ernie.data.WorkoutSession
   import java.text.SimpleDateFormat
   import java.util.Date
   import java.util.Locale
   ```

5. **Create the composable function:**
   - Function signature:
   ```kotlin
   @Composable
   fun WorkoutSessionCard(
       session: WorkoutSession,
       onClick: () -> Unit,
       modifier: Modifier = Modifier
   )
   ```
   - Parameters:
     - `session: WorkoutSession` - The session to display
     - `onClick: () -> Unit` - Called when card is clicked
     - `modifier: Modifier` - For styling and layout

6. **Format the date:**
   - Convert timestamp to readable date string
   - Use `SimpleDateFormat` or `java.time` (if minSdk allows)
   - Format: "MMM dd, yyyy" or "MM/dd/yyyy"
   - Example: "Jan 15, 2024"

7. **Create session summary:**
   - Calculate summary text:
     - Count muscle groups: `session.muscleGroups.size`
     - Count total exercises: `session.getTotalExerciseCount()`
     - Format: "X muscle groups, Y exercises" or "Empty session"

8. **Create Card layout:**
   - Use `Card` component with `clickable` modifier
   - Inside Card, use `Column` for vertical layout
   - Display:
     - Session name (large, bold)
     - Date (smaller, gray)
     - Summary (medium, secondary color)
   - Add padding and spacing

9. **Style the card:**
   - Use Material3 theme colors
   - Add elevation for depth
   - Make it visually appealing

### Expected Code Structure:

```kotlin
package com.journal.ernie.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.journal.ernie.data.WorkoutSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutSessionCard(
    session: WorkoutSession,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Format date
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(session.date))
    
    // Create summary
    val muscleGroupCount = session.muscleGroups.size
    val exerciseCount = session.getTotalExerciseCount()
    val summary = when {
        muscleGroupCount == 0 -> "Empty session"
        exerciseCount == 0 -> "$muscleGroupCount muscle group${if (muscleGroupCount != 1) "s" else ""}"
        else -> "$muscleGroupCount muscle group${if (muscleGroupCount != 1) "s" else ""}, $exerciseCount exercise${if (exerciseCount != 1) "s" else ""}"
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Session name
            Text(
                text = session.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Date
            Text(
                text = dateString,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Summary
            Text(
                text = summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

### Important Notes:

- **Why SimpleDateFormat?** Converts timestamp (Long) to readable date string. Easy to use and works on all Android versions.
- **Why getTotalExerciseCount()?** Uses the helper method from WorkoutSession. Provides total exercises across all muscle groups.
- **Why plural handling?** "1 exercise" vs "2 exercises" - better grammar and UX.
- **Why clickable modifier?** Makes entire card tappable. Better than just a button inside.
- **Why Card component?** Provides Material3 styling, elevation, and rounded corners automatically.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] Card displays session name correctly
- [ ] Date is formatted and displayed
- [ ] Summary shows correct counts
- [ ] Empty session shows "Empty session"
- [ ] Card is clickable
- [ ] Card has proper styling and elevation

---

## Task 4.4: Create WorkoutListScreen and Update AppNavigation

**File Path:** `src/main/java/com/journal/ernie/ui/WorkoutListScreen.kt`

**Purpose:** Create the main workout list screen that displays all sessions, allows creating new ones, and handles navigation.

### Step-by-Step Instructions:

1. **Create the file:**
   - Right-click on `ui` package folder
   - Select "New" → "Kotlin Class/File"
   - Name: `WorkoutListScreen`
   - Type: "File"

2. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.ui
   ```

3. **Add all required imports:**
   ```kotlin
   import androidx.compose.foundation.layout.Box
   import androidx.compose.foundation.layout.Column
   import androidx.compose.foundation.layout.fillMaxSize
   import androidx.compose.foundation.layout.fillMaxWidth
   import androidx.compose.foundation.layout.padding
   import androidx.compose.foundation.lazy.LazyColumn
   import androidx.compose.foundation.lazy.items
   import androidx.compose.material3.FloatingActionButton
   import androidx.compose.material3.Icon
   import androidx.compose.material3.MaterialTheme
   import androidx.compose.material3.Scaffold
   import androidx.compose.material3.Text
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
   import androidx.compose.material.icons.Icons
   import androidx.compose.material.icons.filled.Add
   import androidx.compose.material.icons.filled.ArrowBack
   import androidx.compose.material3.IconButton
   import androidx.compose.material3.TopAppBar
   import com.journal.ernie.Screen
   import com.journal.ernie.data.WorkoutSession
   import com.journal.ernie.ui.components.WorkoutSessionCard
   import com.journal.ernie.ui.dialogs.CreateSessionDialog
   import com.journal.ernie.viewmodel.WorkoutViewModel
   ```

4. **Create the composable function:**
   - Function signature:
   ```kotlin
   @Composable
   fun WorkoutListScreen(
       viewModel: WorkoutViewModel,
       onNavigateTo: (Screen) -> Unit,
       onNavigateBack: () -> Unit
   )
   ```
   - Parameters:
     - `viewModel: WorkoutViewModel` - The ViewModel for workout data
     - `onNavigateTo: (Screen) -> Unit` - Navigation callback
     - `onNavigateBack: () -> Unit` - Back navigation callback

5. **Collect StateFlow values:**
   - Collect allSessions: `val sessions by viewModel.allSessions.collectAsState()`
   - This observes the sessions list and recomposes when it changes

6. **Add dialog state:**
   - `var showCreateDialog by remember { mutableStateOf(false) }`
   - Controls whether CreateSessionDialog is visible

7. **Create Scaffold layout:**
   - Use `Scaffold` component for Material3 structure
   - Add TopAppBar with:
     - Title: "Workout Sessions"
     - Back button (IconButton with ArrowBack icon)
   - Add FloatingActionButton (FAB) with Add icon
   - Add content area for session list

8. **Handle empty state:**
   - If `sessions.isEmpty()`, show message:
     - "No workout sessions yet"
     - "Tap the + button to create your first session"
   - Center the message in a Box

9. **Display session list:**
   - Use `LazyColumn` for efficient scrolling
   - Use `items(sessions) { session -> }` to iterate
   - For each session, display `WorkoutSessionCard`
   - Add padding and spacing

10. **Handle card click:**
    - When card is clicked:
      - Call `viewModel.selectSession(session.id)`
      - Navigate to WorkoutSessionScreen: `onNavigateTo(Screen.WorkoutSession)`

11. **Handle FAB click:**
    - When FAB is clicked: `showCreateDialog = true`

12. **Handle dialog confirm:**
    - When CreateSessionDialog confirms:
      - Call `viewModel.createNewSession(name)`
      - Navigate to WorkoutSessionScreen: `onNavigateTo(Screen.WorkoutSession)`
      - Close dialog: `showCreateDialog = false`

13. **Handle back button:**
    - When back button is clicked: `onNavigateBack()`

### Expected Code Structure:

```kotlin
package com.journal.ernie.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import com.journal.ernie.Screen
import com.journal.ernie.ui.components.WorkoutSessionCard
import com.journal.ernie.ui.dialogs.CreateSessionDialog
import com.journal.ernie.viewmodel.WorkoutViewModel

@Composable
fun WorkoutListScreen(
    viewModel: WorkoutViewModel,
    onNavigateTo: (Screen) -> Unit,
    onNavigateBack: () -> Unit
) {
    // Collect sessions from ViewModel
    val sessions by viewModel.allSessions.collectAsState()
    
    // Dialog state
    var showCreateDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Sessions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Session"
                )
            }
        }
    ) { paddingValues ->
        if (sessions.isEmpty()) {
            // Empty state
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
                        text = "No workout sessions yet",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Tap the + button to create your first session",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            // Session list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                items(sessions) { session ->
                    WorkoutSessionCard(
                        session = session,
                        onClick = {
                            viewModel.selectSession(session.id)
                            onNavigateTo(Screen.WorkoutSession)
                        }
                    )
                }
            }
        }
    }
    
    // Create session dialog
    if (showCreateDialog) {
        CreateSessionDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { sessionName ->
                viewModel.createNewSession(sessionName)
                onNavigateTo(Screen.WorkoutSession)
                showCreateDialog = false
            }
        )
    }
}
```

### Important Notes:

- **Why collectAsState()?** Converts StateFlow to Compose State. Triggers recomposition when sessions change.
- **Why Scaffold?** Provides Material3 structure (TopAppBar, FAB, content area). Handles padding automatically.
- **Why LazyColumn?** Efficiently displays long lists. Only renders visible items.
- **Why items()?** LazyColumn extension function. Automatically handles list iteration and key generation.
- **Why empty state?** Better UX than blank screen. Guides user to create first session.
- **Why selectSession before navigate?** Ensures ViewModel has correct current session before showing WorkoutSessionScreen.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] Screen displays empty state when no sessions
- [ ] FAB is visible and clickable
- [ ] Dialog opens when FAB is clicked
- [ ] Creating session adds it to list
- [ ] Sessions are displayed in list
- [ ] Clicking session card navigates to WorkoutSessionScreen
- [ ] Back button navigates back to Home
- [ ] List scrolls correctly

---

## Task 4.5: Update AppNavigation to Use WorkoutListScreen

**File Path:** `src/main/java/com/journal/ernie/ui/AppNavigation.kt`

**Purpose:** Replace the placeholder WorkoutListScreen with the actual implementation and pass ViewModel.

### Step-by-Step Instructions:

1. **Open AppNavigation.kt:**
   - File should already exist from Phase 1
   - Current content has placeholder for WorkoutListScreen

2. **Add WorkoutViewModel import:**
   - Add: `import com.journal.ernie.viewmodel.WorkoutViewModel`

3. **Update function signature:**
   - Add ViewModel parameter:
     ```kotlin
     fun AppNavigation(
         currentScreen: Screen,
         onNavigateTo: (Screen) -> Unit,
         viewModel: WorkoutViewModel
     )
     ```

4. **Replace WorkoutListScreen placeholder:**
   - Find `is Screen.WorkoutList ->` case
   - Replace placeholder with:
     ```kotlin
     WorkoutListScreen(
         viewModel = viewModel,
         onNavigateTo = onNavigateTo,
         onNavigateBack = { onNavigateTo(Screen.Home) }
     )
     ```

5. **Keep other placeholders:**
   - WorkoutSessionScreen placeholder remains (will be implemented in Phase 5)
   - HomeScreen should already be implemented from Phase 2

### Expected Code Structure (AppNavigation.kt after update):

```kotlin
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
            // Placeholder - WorkoutSessionScreen will be created in Phase 5
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Workout Session Screen")
            }
        }
    }
}
```

### Important Notes:

- **Why pass ViewModel?** WorkoutListScreen needs ViewModel to access sessions and create new ones.
- **Why onNavigateBack?** Provides way to go back to Home screen. Could also use system back button.
- **Why keep WorkoutSessionScreen placeholder?** Phase 5 will implement it. Keeping placeholder prevents crashes.

### Testing Checklist:

After updating AppNavigation, verify:
- [ ] File compiles without errors
- [ ] Navigation from Home to WorkoutList works
- [ ] WorkoutListScreen displays correctly
- [ ] Back navigation works
- [ ] ViewModel is passed correctly

---

## Phase 4 Complete: Final Verification

After completing all tasks, run these comprehensive tests:

### Navigation Flow Test:
- [ ] Home → Workout (center box) → WorkoutListScreen displays
- [ ] WorkoutListScreen → Back button → Returns to Home
- [ ] WorkoutListScreen → Create session → Navigates to WorkoutSessionScreen (placeholder)

### Session Creation Test:
- [ ] FAB opens CreateSessionDialog
- [ ] Dialog displays correctly
- [ ] Can type session name
- [ ] Create button disabled when empty
- [ ] Create button enabled when text entered
- [ ] Creating session adds it to list
- [ ] New session becomes current session
- [ ] Navigation to WorkoutSessionScreen happens after creation

### Session List Test:
- [ ] Empty state displays when no sessions
- [ ] Sessions display in list when they exist
- [ ] Session cards show correct information (name, date, summary)
- [ ] Clicking session card selects it and navigates
- [ ] List scrolls correctly
- [ ] Multiple sessions display correctly

### UI/UX Test:
- [ ] TopAppBar displays "Workout Sessions" title
- [ ] Back button is visible and functional
- [ ] FAB is visible and positioned correctly
- [ ] Cards have proper styling and elevation
- [ ] Empty state message is clear and helpful
- [ ] Dialog is properly styled and centered

### Integration Test:
- [ ] ViewModel state persists across navigation
- [ ] Creating session updates ViewModel
- [ ] Selecting session updates ViewModel
- [ ] StateFlow updates trigger UI recomposition
- [ ] No crashes or errors in logcat

### Code Quality:
- [ ] All files compile without errors
- [ ] No unused imports
- [ ] Code follows Kotlin conventions
- [ ] Components are reusable where appropriate
- [ ] Error handling is present (empty checks, null safety)

---

## Common Issues & Solutions

### Issue: "Unresolved reference: viewModel"
**Solution:** 
- Add import: `import androidx.lifecycle.viewmodel.compose.viewModel`
- Ensure lifecycle-viewmodel-compose dependency is in build.gradle (but don't modify Gradle per instructions)

### Issue: "Unresolved reference: collectAsState"
**Solution:** 
- Add import: `import androidx.compose.runtime.collectAsState`
- Ensure compose-runtime dependency is in build.gradle

### Issue: Dialog doesn't dismiss
**Solution:**
- Check that `onDismiss()` is called in both Cancel and Create button handlers
- Verify `showCreateDialog = false` is set after confirming

### Issue: Sessions don't appear in list
**Solution:**
- Verify `collectAsState()` is used to observe StateFlow
- Check that ViewModel's `createNewSession()` is being called
- Verify ViewModel is passed correctly to WorkoutListScreen
- Check logcat for any errors

### Issue: "Cannot find WorkoutSessionCard"
**Solution:**
- Verify WorkoutSessionCard.kt is in `ui/components/` package
- Check import statement: `import com.journal.ernie.ui.components.WorkoutSessionCard`

### Issue: Date formatting error
**Solution:**
- Check that `SimpleDateFormat` import is correct
- Verify `session.date` is a Long timestamp
- Test date formatting with sample timestamp

### Issue: FAB not visible
**Solution:**
- Check that Scaffold's `floatingActionButton` parameter is set
- Verify FAB is not hidden behind other content
- Check Material3 theme is applied

### Issue: Navigation doesn't work
**Solution:**
- Verify `onNavigateTo` callback is passed correctly
- Check that Screen.WorkoutSession exists in Screen sealed class
- Verify ViewModel's `selectSession()` is called before navigation

---

## Architecture Notes

### Data Flow:

```
User Action (Tap FAB)
    ↓
WorkoutListScreen (shows dialog)
    ↓
User Enters Name & Confirms
    ↓
CreateSessionDialog (calls onConfirm)
    ↓
WorkoutListScreen (calls viewModel.createNewSession)
    ↓
WorkoutViewModel (updates StateFlow)
    ↓
StateFlow Emits New Value
    ↓
WorkoutListScreen (collectAsState recomposes)
    ↓
UI Updates with New Session
```

### Component Hierarchy:

```
AppNavigation
    ↓
WorkoutListScreen
    ├── Scaffold
    │   ├── TopAppBar
    │   ├── LazyColumn
    │   │   └── WorkoutSessionCard (for each session)
    │   └── FloatingActionButton
    └── CreateSessionDialog (conditional)
```

### State Management:

- **ViewModel:** Holds all workout data in StateFlow
- **UI:** Observes StateFlow with `collectAsState()`
- **Updates:** ViewModel functions modify StateFlow, UI automatically recomposes

---

## Next Steps: Phase 5

After Phase 4 is complete and verified, you're ready for Phase 5:
- Create WorkoutSessionScreen layout
- Add TimerDisplay component
- Display muscle groups (static data first)
- This will show the active workout session with timer and exercise tracking

**Phase 4 establishes the session list and creation flow. Phase 5 will implement the actual workout tracking screen.**

---

## Summary

Phase 4 creates:
- ✅ MainActivity ViewModel initialization
- ✅ CreateSessionDialog for creating new sessions
- ✅ WorkoutSessionCard component for displaying sessions
- ✅ WorkoutListScreen with list, FAB, and empty state
- ✅ AppNavigation integration with ViewModel

**Total Files Created:** 3 new files (CreateSessionDialog.kt, WorkoutSessionCard.kt, WorkoutListScreen.kt)
**Total Files Modified:** 2 files (MainActivity.kt, AppNavigation.kt)
**Total Lines of Code:** ~400-500 lines
**Time Estimate:** 2-3 hours for careful implementation

The workout list screen is now functional and ready for users to create and select workout sessions. The ViewModel integration is complete, and the app is ready for Phase 5 implementation.

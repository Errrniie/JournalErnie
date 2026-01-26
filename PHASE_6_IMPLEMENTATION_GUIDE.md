# Phase 6: Muscle Group Management - Detailed Implementation Guide

## Overview

This document provides step-by-step instructions for implementing Phase 6 of the Workout Tracking App. Phase 6 adds muscle group management functionality, allowing users to add and remove muscle groups from their workout sessions. This includes creating a dialog for adding muscle groups with preset suggestions and enhancing the muscle group display.

**Goal:** Enable users to add muscle groups to their workout sessions using preset suggestions or custom names, and remove muscle groups. The ViewModel functions for this were already implemented in Phase 3, so this phase focuses on the UI components and wiring them up.

**Prerequisites:** 
- Phase 1 must be completed (all data models exist, including MuscleGroupPresets)
- Phase 3 must be completed (WorkoutViewModel with addMuscleGroup/removeMuscleGroup functions)
- Phase 5 must be completed (WorkoutSessionScreen with basic layout)
- Understanding of Compose dialogs and state management
- Basic knowledge of Material3 components (Dialog, TextField, DropdownMenu, Button)

---

## Phase 6 Overview

Phase 6 consists of 4 main tasks:
1. Create AddMuscleGroupDialog with preset suggestions and custom input
2. Create MuscleGroupCard component for displaying muscle groups with exercises
3. Update WorkoutSessionScreen to integrate muscle group management
4. Wire up add/remove functionality with ViewModel

**Estimated Time:** 2-3 hours
**Files to Create:** 2 new files
**Files to Modify:** 1 existing file

---

## Task 6.1: Create AddMuscleGroupDialog

**File Path:** `src/main/java/com/journal/ernie/ui/dialogs/AddMuscleGroupDialog.kt`

**Purpose:** Create a dialog that allows users to add a muscle group by selecting from presets or entering a custom name.

### Step-by-Step Instructions:

1. **Create the file (if dialogs folder doesn't exist, it should from Phase 4):**
   - Right-click on `dialogs` package folder (under `ui`)
   - Select "New" → "Kotlin Class/File"
   - Name: `AddMuscleGroupDialog`
   - Type: "File"

2. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.ui.dialogs
   ```

3. **Add all required imports:**
   ```kotlin
   import androidx.compose.foundation.clickable
   import androidx.compose.foundation.layout.Arrangement
   import androidx.compose.foundation.layout.Column
   import androidx.compose.foundation.layout.Row
   import androidx.compose.foundation.layout.Spacer
   import androidx.compose.foundation.layout.fillMaxWidth
   import androidx.compose.foundation.layout.height
   import androidx.compose.foundation.layout.padding
   import androidx.compose.foundation.layout.width
   import androidx.compose.foundation.lazy.LazyColumn
   import androidx.compose.foundation.lazy.items
   import androidx.compose.material3.Button
   import androidx.compose.material3.Card
   import androidx.compose.material3.DropdownMenu
   import androidx.compose.material3.DropdownMenuItem
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
   import com.journal.ernie.data.MuscleGroupPresets
   ```

4. **Create the composable function:**
   - Function signature:
   ```kotlin
   @Composable
   fun AddMuscleGroupDialog(
       onDismiss: () -> Unit,
       onConfirm: (String) -> Unit
   )
   ```
   - Parameters:
     - `onDismiss: () -> Unit` - Called when user cancels or dismisses dialog
     - `onConfirm: (String) -> Unit` - Called when user confirms, passes muscle group name

5. **Add state for text field and dropdown:**
   - `var muscleGroupName by rememberSaveable { mutableStateOf("") }`
   - `var showPresetDropdown by remember { mutableStateOf(false) }`
   - `var presetSuggestions by remember { mutableStateOf(MuscleGroupPresets.getAllPresets()) }`
   - Use `rememberSaveable` for text to preserve on configuration changes

6. **Add preset filtering logic:**
   - When user types, filter presets: `presetSuggestions = MuscleGroupPresets.getPresetSuggestions(muscleGroupName)`
   - Show dropdown when text field is focused and suggestions exist

7. **Create Dialog composable:**
   - Use `Dialog(onDismissRequest = onDismiss) { }`
   - Inside Dialog, create a Card for the dialog content

8. **Add dialog content:**
   - Column layout with:
     - Title: "Add Muscle Group"
     - TextField for muscle group name
     - DropdownMenu showing preset suggestions (when typing)
     - Row with Cancel and Add buttons

9. **Implement preset selection:**
   - When user clicks a preset, set `muscleGroupName = preset`
   - Hide dropdown after selection
   - Focus text field for editing

10. **Add validation:**
    - Disable Add button if `muscleGroupName.trim().isEmpty()`
    - Trim whitespace before confirming

11. **Handle button clicks:**
    - Cancel button: Call `onDismiss()`
    - Add button: Call `onConfirm(muscleGroupName.trim())` then `onDismiss()`

12. **Style the dialog:**
    - Use Material3 theme colors
    - Make preset suggestions clickable and visually distinct
    - Add proper spacing and padding

### Expected Code Structure:

```kotlin
package com.journal.ernie.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
                            elevation = androidx.compose.material3.CardDefaults.cardElevation(
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
```

### Important Notes:

- **Why rememberSaveable?** Preserves text input when screen rotates. Better UX than losing what user typed.
- **Why preset suggestions?** Helps users discover common muscle groups. Reduces typing and errors.
- **Why filter suggestions?** Shows relevant presets as user types. Better than showing all presets always.
- **Why LazyColumn for suggestions?** Efficiently displays list of presets. Only renders visible items.
- **Why trim()?** Removes leading/trailing whitespace. Prevents muscle groups with names like "  Chest  ".
- **Why enabled check?** Prevents adding muscle groups with empty names. Better validation.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] Dialog displays when called
- [ ] Text field accepts input
- [ ] Preset suggestions appear when typing
- [ ] Clicking preset fills text field
- [ ] Add button is disabled when text is empty
- [ ] Add button is enabled when text is entered
- [ ] Cancel button dismisses dialog
- [ ] Add button calls onConfirm with trimmed name
- [ ] Dialog dismisses after Add is clicked

---

## Task 6.2: Create MuscleGroupCard Component

**File Path:** `src/main/java/com/journal/ernie/ui/components/MuscleGroupCard.kt`

**Purpose:** Create a reusable card component that displays a muscle group with its exercises and provides buttons to add exercises and remove the group.

### Step-by-Step Instructions:

1. **Create the file (if components folder doesn't exist, it should from Phase 4/5):**
   - Right-click on `components` package folder (under `ui`)
   - Select "New" → "Kotlin Class/File"
   - Name: `MuscleGroupCard`
   - Type: "File"

2. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.ui.components
   ```

3. **Add all required imports:**
   ```kotlin
   import androidx.compose.foundation.layout.Arrangement
   import androidx.compose.foundation.layout.Column
   import androidx.compose.foundation.layout.Row
   import androidx.compose.foundation.layout.Spacer
   import androidx.compose.foundation.layout.fillMaxWidth
   import androidx.compose.foundation.layout.height
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
   ```

4. **Create the composable function:**
   - Function signature:
   ```kotlin
   @Composable
   fun MuscleGroupCard(
       muscleGroup: MuscleGroup,
       onAddExercise: () -> Unit,
       onRemoveGroup: () -> Unit,
       modifier: Modifier = Modifier
   )
   ```
   - Parameters:
     - `muscleGroup: MuscleGroup` - The muscle group to display
     - `onAddExercise: () -> Unit` - Callback when add exercise button is clicked
     - `onRemoveGroup: () -> Unit` - Callback when remove group button is clicked
     - `modifier: Modifier` - For styling and layout

5. **Create Card layout:**
   - Use `Card` component for container
   - Inside Card, use `Column` for vertical layout
   - Display:
     - Row with muscle group name and delete button
     - Exercise count or list (for now, just count - exercises will be added in Phase 7)
     - Add exercise button

6. **Display muscle group name:**
   - Large, bold text at top
   - Use Material3 typography

7. **Display exercise information:**
   - Show exercise count: "X exercises" or "No exercises yet"
   - For now, just show count (exercise cards will be added in Phase 7)
   - Use secondary text color

8. **Add action buttons:**
   - Add Exercise button: OutlinedButton with Add icon
   - Remove Group button: IconButton with Delete icon (in header row)

9. **Style the component:**
   - Card with elevation
   - Proper padding and spacing
   - Material3 theme colors

### Expected Code Structure:

```kotlin
package com.journal.ernie.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Muscle Group",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // Exercise count
            if (muscleGroup.exercises.isEmpty()) {
                Text(
                    text = "No exercises yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "${muscleGroup.exercises.size} exercise${if (muscleGroup.exercises.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Add exercise button
            OutlinedButton(
                onClick = onAddExercise,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Exercise",
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text("Add Exercise")
            }
        }
    }
}
```

### Important Notes:

- **Why separate callbacks?** Allows parent to handle add exercise and remove group differently. More flexible.
- **Why delete icon in header?** Easy to find and access. Common pattern in Material Design.
- **Why exercise count for now?** Phase 7 will add ExerciseCard components. This is a placeholder.
- **Why OutlinedButton for Add Exercise?** Less prominent than filled button. Appropriate for secondary action.
- **Why error color for delete?** Indicates destructive action. Standard Material Design pattern.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] Card displays muscle group name correctly
- [ ] Exercise count displays correctly
- [ ] "No exercises yet" shows when empty
- [ ] Add Exercise button is visible and clickable
- [ ] Remove Group button is visible and clickable
- [ ] Card has proper styling and elevation
- [ ] Layout is properly spaced

---

## Task 6.3: Update WorkoutSessionScreen to Integrate Muscle Group Management

**File Path:** `src/main/java/com/journal/ernie/ui/WorkoutSessionScreen.kt`

**Purpose:** Replace the basic muscle group display with the full MuscleGroupCard component and add functionality to add/remove muscle groups.

### Step-by-Step Instructions:

1. **Open WorkoutSessionScreen.kt:**
   - File should already exist from Phase 5
   - Current content has basic muscle group display

2. **Add imports:**
   - Add: `import com.journal.ernie.ui.components.MuscleGroupCard`
   - Add: `import com.journal.ernie.ui.dialogs.AddMuscleGroupDialog`
   - Add: `import androidx.compose.material.icons.filled.Add`
   - Add: `import androidx.compose.material3.FloatingActionButton`
   - Add: `import androidx.compose.material3.Icon`

3. **Add dialog state:**
   - Inside composable, add: `var showAddMuscleGroupDialog by remember { mutableStateOf(false) }`
   - Controls whether AddMuscleGroupDialog is visible

4. **Replace MuscleGroupCardBasic with MuscleGroupCard:**
   - Find the `items(currentSession.muscleGroups)` section
   - Replace `MuscleGroupCardBasic(muscleGroup = muscleGroup)` with:
     ```kotlin
     MuscleGroupCard(
         muscleGroup = muscleGroup,
         onAddExercise = {
             // Will be implemented in Phase 7
             // For now, can show placeholder or do nothing
         },
         onRemoveGroup = {
             viewModel.removeMuscleGroup(muscleGroup.id)
         }
     )
     ```

5. **Add FAB for adding muscle groups:**
   - In Scaffold, add `floatingActionButton` parameter:
     ```kotlin
     floatingActionButton = {
         FloatingActionButton(
             onClick = { showAddMuscleGroupDialog = true }
         ) {
             Icon(
                 imageVector = Icons.Default.Add,
                 contentDescription = "Add Muscle Group"
             )
         }
     }
     ```

6. **Add AddMuscleGroupDialog:**
   - After Scaffold, add conditional dialog:
     ```kotlin
     if (showAddMuscleGroupDialog) {
         AddMuscleGroupDialog(
             onDismiss = { showAddMuscleGroupDialog = false },
             onConfirm = { muscleGroupName ->
                 viewModel.addMuscleGroup(muscleGroupName)
                 showAddMuscleGroupDialog = false
             }
         )
     }
     ```

7. **Remove MuscleGroupCardBasic function:**
   - Delete the `MuscleGroupCardBasic` composable function
   - It's no longer needed

8. **Update empty state:**
   - Update empty state message to mention FAB:
     - "No muscle groups yet"
     - "Tap the + button to add a muscle group"

### Expected Code Structure (WorkoutSessionScreen.kt after update):

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
import androidx.compose.ui.unit.dp
import com.journal.ernie.ui.components.MuscleGroupCard
import com.journal.ernie.ui.components.TimerDisplay
import com.journal.ernie.ui.dialogs.AddMuscleGroupDialog
import com.journal.ernie.viewmodel.WorkoutViewModel

@Composable
fun WorkoutSessionScreen(
    viewModel: WorkoutViewModel,
    onNavigateBack: () -> Unit
) {
    // Collect state from ViewModel
    val currentSession by viewModel.currentSession.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    
    // Dialog state
    var showAddMuscleGroupDialog by remember { mutableStateOf(false) }
    
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
                            imageVector = Icons.Default.ArrowBack,
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
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Muscle Group"
                )
            }
        }
    ) { paddingValues ->
        if (currentSession == null) {
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
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
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
                if (currentSession.muscleGroups.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text(
                                    text = "No muscle groups yet",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tap the + button to add a muscle group",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(currentSession.muscleGroups) { muscleGroup ->
                        MuscleGroupCard(
                            muscleGroup = muscleGroup,
                            onAddExercise = {
                                // Will be implemented in Phase 7
                            },
                            onRemoveGroup = {
                                viewModel.removeMuscleGroup(muscleGroup.id)
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
}
```

### Important Notes:

- **Why FAB for adding?** Standard Material Design pattern for primary action. Easy to find and tap.
- **Why dialog state?** Controls when dialog is visible. Better than always rendering it.
- **Why call ViewModel functions?** ViewModel manages state. UI just triggers actions.
- **Why placeholder for onAddExercise?** Phase 7 will implement exercise management. This is preparation.
- **Why remove MuscleGroupCardBasic?** Replaced by full MuscleGroupCard component. No longer needed.

### Testing Checklist:

After updating this file, verify:
- [ ] File compiles without errors
- [ ] FAB is visible and clickable
- [ ] Dialog opens when FAB is clicked
- [ ] Adding muscle group adds it to list
- [ ] Muscle groups display with MuscleGroupCard
- [ ] Remove button removes muscle group
- [ ] Empty state shows when no muscle groups
- [ ] UI updates when muscle groups are added/removed

---

## Task 6.4: Test Muscle Group Management Flow

**Purpose:** Verify that the complete muscle group management flow works correctly.

### Step-by-Step Testing:

1. **Test adding muscle group:**
   - Navigate to WorkoutSessionScreen
   - Tap FAB
   - Verify dialog opens
   - Type muscle group name or select preset
   - Tap Add
   - Verify muscle group appears in list

2. **Test preset suggestions:**
   - Open AddMuscleGroupDialog
   - Type "Ch" in text field
   - Verify "Chest" appears in suggestions
   - Tap "Chest"
   - Verify text field is filled
   - Tap Add
   - Verify muscle group is added

3. **Test custom muscle group:**
   - Open AddMuscleGroupDialog
   - Type custom name (not a preset)
   - Tap Add
   - Verify custom muscle group is added

4. **Test removing muscle group:**
   - Add a muscle group
   - Tap delete icon on muscle group card
   - Verify muscle group is removed from list

5. **Test validation:**
   - Open AddMuscleGroupDialog
   - Leave text field empty
   - Verify Add button is disabled
   - Type text
   - Verify Add button is enabled

6. **Test multiple muscle groups:**
   - Add multiple muscle groups
   - Verify all display in list
   - Verify each can be removed independently

### Expected Behavior:

- **Adding muscle group:** Dialog opens → User enters/selects name → Muscle group appears in list
- **Removing muscle group:** User taps delete → Muscle group disappears from list
- **Preset suggestions:** User types → Relevant presets appear → User can select preset
- **Validation:** Empty name → Add button disabled → Valid name → Add button enabled

### Testing Checklist:

- [ ] Adding muscle group works
- [ ] Removing muscle group works
- [ ] Preset suggestions work
- [ ] Custom names work
- [ ] Validation works
- [ ] Multiple muscle groups work
- [ ] UI updates correctly
- [ ] No crashes or errors

---

## Phase 6 Complete: Final Verification

After completing all tasks, run these comprehensive tests:

### Muscle Group Management Test:
- [ ] FAB opens AddMuscleGroupDialog
- [ ] Dialog displays correctly
- [ ] Preset suggestions appear when typing
- [ ] Selecting preset fills text field
- [ ] Custom names can be entered
- [ ] Add button disabled when empty
- [ ] Add button enabled when text entered
- [ ] Adding muscle group adds it to list
- [ ] Muscle groups display with MuscleGroupCard
- [ ] Remove button removes muscle group
- [ ] Multiple muscle groups can be added
- [ ] Each muscle group can be removed independently

### UI/UX Test:
- [ ] FAB is visible and positioned correctly
- [ ] Dialog is properly styled and centered
- [ ] MuscleGroupCard displays correctly
- [ ] Exercise count shows correctly
- [ ] Empty state message is clear
- [ ] Layout is scrollable
- [ ] Spacing and padding are consistent

### Integration Test:
- [ ] ViewModel functions are called correctly
- [ ] State updates trigger UI recomposition
- [ ] Muscle groups persist in current session
- [ ] No crashes or errors in logcat
- [ ] State persists across navigation

### Code Quality:
- [ ] All files compile without errors
- [ ] No unused imports
- [ ] Code follows Kotlin conventions
- [ ] Components are reusable
- [ ] Error handling is present

---

## Common Issues & Solutions

### Issue: "Unresolved reference: MuscleGroupPresets"
**Solution:** 
- Verify MuscleGroupPresets.kt exists in `data` package
- Check import: `import com.journal.ernie.data.MuscleGroupPresets`

### Issue: Preset suggestions don't appear
**Solution:**
- Check that `getPresetSuggestions()` is called correctly
- Verify text field value is being observed
- Check that `showPresetDropdown` state is managed correctly
- Verify LazyColumn is rendering correctly

### Issue: Dialog doesn't dismiss
**Solution:**
- Check that `onDismiss()` is called in both Cancel and Add button handlers
- Verify `showAddMuscleGroupDialog = false` is set after confirming

### Issue: Muscle group doesn't appear after adding
**Solution:**
- Verify ViewModel's `addMuscleGroup()` is being called
- Check that `collectAsState()` is used to observe currentSession
- Verify ViewModel is passed correctly to WorkoutSessionScreen
- Check logcat for any errors

### Issue: Remove button doesn't work
**Solution:**
- Verify `onRemoveGroup` callback is passed correctly
- Check that ViewModel's `removeMuscleGroup()` is being called
- Verify muscle group ID is correct
- Check logcat for any errors

### Issue: FAB not visible
**Solution:**
- Check that Scaffold's `floatingActionButton` parameter is set
- Verify FAB is not hidden behind other content
- Check Material3 theme is applied

### Issue: "Cannot find MuscleGroupCard"
**Solution:**
- Verify MuscleGroupCard.kt is in `ui/components/` package
- Check import: `import com.journal.ernie.ui.components.MuscleGroupCard`

---

## Architecture Notes

### Data Flow:

```
User Action (Tap FAB)
    ↓
WorkoutSessionScreen (shows dialog)
    ↓
User Enters/Selects Name & Confirms
    ↓
AddMuscleGroupDialog (calls onConfirm)
    ↓
WorkoutSessionScreen (calls viewModel.addMuscleGroup)
    ↓
WorkoutViewModel (updates StateFlow)
    ↓
StateFlow Emits New Value
    ↓
WorkoutSessionScreen (collectAsState recomposes)
    ↓
UI Updates with New Muscle Group
```

### Component Hierarchy:

```
WorkoutSessionScreen
    ├── Scaffold
    │   ├── TopAppBar
    │   ├── LazyColumn
    │   │   ├── TimerDisplay
    │   │   └── MuscleGroupCard (for each group)
    │   └── FloatingActionButton
    └── AddMuscleGroupDialog (conditional)
```

### State Management:

- **ViewModel:** Holds current session in StateFlow (already implemented in Phase 3)
- **UI:** Observes StateFlow with `collectAsState()`
- **Updates:** ViewModel functions modify StateFlow, UI automatically recomposes
- **Dialog State:** Managed locally in WorkoutSessionScreen

---

## Next Steps: Phase 7

After Phase 6 is complete and verified, you're ready for Phase 7:
- Create ExerciseCard component
- Create AddExerciseDialog
- Implement add/remove exercise in ViewModel (already done in Phase 3)
- Wire up UI for exercise management
- This will allow users to add and manage exercises within muscle groups

**Phase 6 establishes muscle group management. Phase 7 will add exercise management functionality.**

---

## Summary

Phase 6 creates:
- ✅ AddMuscleGroupDialog with preset suggestions
- ✅ MuscleGroupCard component for displaying muscle groups
- ✅ WorkoutSessionScreen integration with muscle group management
- ✅ FAB for adding muscle groups
- ✅ Remove functionality for muscle groups

**Total Files Created:** 2 new files (AddMuscleGroupDialog.kt, MuscleGroupCard.kt)
**Total Files Modified:** 1 file (WorkoutSessionScreen.kt)
**Total Lines of Code:** ~400-500 lines
**Time Estimate:** 2-3 hours for careful implementation

The muscle group management is now functional. Users can add muscle groups using presets or custom names, and remove them. The foundation is ready for Phase 7 exercise management.

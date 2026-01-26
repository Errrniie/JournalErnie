# Phase 7: Exercise Management - Detailed Implementation Guide

## Overview

This document provides step-by-step instructions for implementing Phase 7 of the Workout Tracking App. Phase 7 adds exercise management functionality, allowing users to add and remove exercises within muscle groups. This includes creating a dialog for adding exercises and displaying exercises within muscle group cards.

**Goal:** Enable users to add exercises to muscle groups and remove exercises. The ViewModel functions for this were already implemented in Phase 3, so this phase focuses on the UI components and wiring them up. Sets will be displayed but not yet editable (that's Phase 8).

**Prerequisites:** 
- Phase 1 must be completed (all data models exist)
- Phase 3 must be completed (WorkoutViewModel with addExercise/removeExercise functions)
- Phase 6 must be completed (MuscleGroupCard component exists)
- Understanding of Compose dialogs and nested components
- Basic knowledge of Material3 components (Dialog, TextField, Card, Button)

---

## Phase 7 Overview

Phase 7 consists of 4 main tasks:
1. Create AddExerciseDialog for adding new exercises
2. Create ExerciseCard component for displaying exercises with sets
3. Update MuscleGroupCard to display ExerciseCard components
4. Wire up add/remove functionality with ViewModel

**Estimated Time:** 2-3 hours
**Files to Create:** 2 new files
**Files to Modify:** 1 existing file

---

## Task 7.1: Create AddExerciseDialog

**File Path:** `src/main/java/com/journal/ernie/ui/dialogs/AddExerciseDialog.kt`

**Purpose:** Create a dialog that allows users to add an exercise to a muscle group by entering an exercise name.

### Step-by-Step Instructions:

1. **Create the file (dialogs folder should exist from Phase 4/6):**
   - Right-click on `dialogs` package folder (under `ui`)
   - Select "New" → "Kotlin Class/File"
   - Name: `AddExerciseDialog`
   - Type: "File"

2. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.ui.dialogs
   ```

3. **Add all required imports:**
   ```kotlin
   import androidx.compose.foundation.layout.Arrangement
   import androidx.compose.foundation.layout.Column
   import androidx.compose.foundation.layout.Row
   import androidx.compose.foundation.layout.Spacer
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
   import androidx.compose.runtime.saveable.rememberSaveable
   import androidx.compose.runtime.setValue
   import androidx.compose.ui.Modifier
   import androidx.compose.ui.text.font.FontWeight
   import androidx.compose.ui.unit.dp
   import androidx.compose.ui.window.Dialog
   ```

4. **Create the composable function:**
   - Function signature:
   ```kotlin
   @Composable
   fun AddExerciseDialog(
       onDismiss: () -> Unit,
       onConfirm: (String) -> Unit
   )
   ```
   - Parameters:
     - `onDismiss: () -> Unit` - Called when user cancels or dismisses dialog
     - `onConfirm: (String) -> Unit` - Called when user confirms, passes exercise name

5. **Add state for text field:**
   - `var exerciseName by rememberSaveable { mutableStateOf("") }`
   - Use `rememberSaveable` to preserve text on configuration changes

6. **Create Dialog composable:**
   - Use `Dialog(onDismissRequest = onDismiss) { }`
   - Inside Dialog, create a Card for the dialog content

7. **Add dialog content:**
   - Column layout with:
     - Title: "Add Exercise"
     - TextField for exercise name
     - Row with Cancel and Add buttons

8. **Add validation:**
   - Disable Add button if `exerciseName.trim().isEmpty()`
   - Trim whitespace before confirming

9. **Handle button clicks:**
   - Cancel button: Call `onDismiss()`
   - Add button: Call `onConfirm(exerciseName.trim())` then `onDismiss()`

10. **Style the dialog:**
    - Use Material3 theme colors
    - Add proper spacing and padding
    - Make it consistent with AddMuscleGroupDialog

### Expected Code Structure:

```kotlin
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
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise Name") },
                    placeholder = { Text("e.g., Bench Press, Squats") },
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
                            val trimmedName = exerciseName.trim()
                            if (trimmedName.isNotEmpty()) {
                                onConfirm(trimmedName)
                                onDismiss()
                            }
                        },
                        enabled = exerciseName.trim().isNotEmpty()
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
- **Why trim()?** Removes leading/trailing whitespace. Prevents exercises with names like "  Bench Press  ".
- **Why enabled check?** Prevents adding exercises with empty names. Better validation.
- **Why singleLine?** Exercise names should be single line. Prevents multiline input.
- **Why similar to AddMuscleGroupDialog?** Consistency in UI/UX. Users familiar with one dialog understand the other.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] Dialog displays when called
- [ ] Text field accepts input
- [ ] Add button is disabled when text is empty
- [ ] Add button is enabled when text is entered
- [ ] Cancel button dismisses dialog
- [ ] Add button calls onConfirm with trimmed name
- [ ] Dialog dismisses after Add is clicked

---

## Task 7.2: Create ExerciseCard Component

**File Path:** `src/main/java/com/journal/ernie/ui/components/ExerciseCard.kt`

**Purpose:** Create a reusable card component that displays an exercise with its sets and provides buttons to add sets and remove the exercise.

### Step-by-Step Instructions:

1. **Create the file (components folder should exist from Phase 4/5/6):**
   - Right-click on `components` package folder (under `ui`)
   - Select "New" → "Kotlin Class/File"
   - Name: `ExerciseCard`
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
   import com.journal.ernie.data.Exercise
   import com.journal.ernie.data.SetEntry
   ```

4. **Create the composable function:**
   - Function signature:
   ```kotlin
   @Composable
   fun ExerciseCard(
       exercise: Exercise,
       onAddSet: () -> Unit,
       onRemoveExercise: () -> Unit,
       modifier: Modifier = Modifier
   )
   ```
   - Parameters:
     - `exercise: Exercise` - The exercise to display
     - `onAddSet: () -> Unit` - Callback when add set button is clicked
     - `onRemoveExercise: () -> Unit` - Callback when remove exercise button is clicked
     - `modifier: Modifier` - For styling and layout

5. **Create Card layout:**
   - Use `Card` component for container
   - Inside Card, use `Column` for vertical layout
   - Display:
     - Row with exercise name and delete button
     - Sets list (for now, just show count or basic info - SetRow will be added in Phase 8)
     - Add set button

6. **Display exercise name:**
   - Medium, bold text
   - Use Material3 typography

7. **Display sets information:**
   - Show set count: "X sets" or "No sets yet"
   - For now, just show count (SetRow components will be added in Phase 8)
   - Optionally show basic set info (reps/weight) in a simple format

8. **Add action buttons:**
   - Add Set button: OutlinedButton with Add icon
   - Remove Exercise button: IconButton with Delete icon (in header row)

9. **Style the component:**
   - Card with elevation (lower than MuscleGroupCard for hierarchy)
   - Proper padding and spacing
   - Material3 theme colors
   - Indented/nested appearance to show it's within a muscle group

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
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Exercise",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // Sets information
            if (exercise.sets.isEmpty()) {
                Text(
                    text = "No sets yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column {
                    Text(
                        text = "${exercise.sets.size} set${if (exercise.sets.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    // Basic set info display (will be enhanced in Phase 8 with SetRow)
                    exercise.sets.forEachIndexed { index, set ->
                        Text(
                            text = "Set ${index + 1}: ${set.reps} reps × ${set.weight}kg${if (set.comment != null) " - ${set.comment}" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
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
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Set",
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text("Add Set")
            }
        }
    }
}
```

### Important Notes:

- **Why nested appearance?** Exercise cards are within muscle groups. Visual hierarchy shows this relationship.
- **Why lower elevation?** Creates visual nesting. MuscleGroupCard has higher elevation, ExerciseCard has lower.
- **Why surfaceVariant color?** Subtle background color differentiates from parent card. Shows it's a child component.
- **Why basic set display?** Phase 8 will add SetRow components for full set editing. This is a placeholder.
- **Why separate callbacks?** Allows parent to handle add set and remove exercise differently. More flexible.
- **Why error color for delete?** Indicates destructive action. Standard Material Design pattern.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] Card displays exercise name correctly
- [ ] Set count displays correctly
- [ ] Basic set info displays (reps, weight, comment)
- [ ] "No sets yet" shows when empty
- [ ] Add Set button is visible and clickable
- [ ] Remove Exercise button is visible and clickable
- [ ] Card has proper styling and nested appearance
- [ ] Layout is properly spaced

---

## Task 7.3: Update MuscleGroupCard to Display ExerciseCard Components

**File Path:** `src/main/java/com/journal/ernie/ui/components/MuscleGroupCard.kt`

**Purpose:** Update MuscleGroupCard to display ExerciseCard components for each exercise in the muscle group, and wire up the add exercise functionality.

### Step-by-Step Instructions:

1. **Open MuscleGroupCard.kt:**
   - File should already exist from Phase 6
   - Current content shows exercise count but no exercise cards

2. **Add imports:**
   - Add: `import com.journal.ernie.ui.components.ExerciseCard`
   - Add: `import androidx.compose.foundation.layout.Arrangement`
   - Add: `import androidx.compose.foundation.layout.spacedBy`

3. **Update function signature:**
   - Add parameter for exercise callbacks:
     ```kotlin
     fun MuscleGroupCard(
         muscleGroup: MuscleGroup,
         onAddExercise: () -> Unit,
         onRemoveGroup: () -> Unit,
         onAddSet: (String) -> Unit,  // exerciseId
         onRemoveExercise: (String) -> Unit,  // exerciseId
         modifier: Modifier = Modifier
     )
     ```
   - New parameters:
     - `onAddSet: (String) -> Unit` - Callback with exerciseId when add set is clicked
     - `onRemoveExercise: (String) -> Unit` - Callback with exerciseId when remove exercise is clicked

4. **Replace exercise count with ExerciseCard list:**
   - Find the exercise count display section
   - Replace with:
     ```kotlin
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
                     onRemoveExercise = { onRemoveExercise(exercise.id) }
                 )
             }
         }
     }
     ```

5. **Update spacing:**
   - Ensure proper spacing between exercises
   - Maintain spacing between muscle group name and exercises

### Expected Code Structure (MuscleGroupCard.kt after update):

```kotlin
package com.journal.ernie.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.spacedBy
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
                            onRemoveExercise = { onRemoveExercise(exercise.id) }
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

- **Why pass exerciseId in callbacks?** Need to identify which exercise to add set to or remove. ID is the reliable identifier.
- **Why forEach instead of items?** ExerciseCard is not in a LazyColumn. Regular Column with forEach is simpler.
- **Why spacedBy?** Provides consistent spacing between exercise cards. Better than manual Spacer components.
- **Why keep empty state?** Better UX than blank space. Guides user to add exercises.

### Testing Checklist:

After updating this file, verify:
- [ ] File compiles without errors
- [ ] ExerciseCard components display for each exercise
- [ ] Empty state shows when no exercises
- [ ] Exercises are properly spaced
- [ ] Nested appearance is clear (ExerciseCard within MuscleGroupCard)
- [ ] Callbacks are passed correctly

---

## Task 7.4: Update WorkoutSessionScreen to Wire Up Exercise Management

**File Path:** `src/main/java/com/journal/ernie/ui/WorkoutSessionScreen.kt`

**Purpose:** Wire up the exercise management functionality, including the AddExerciseDialog and connecting it to the ViewModel.

### Step-by-Step Instructions:

1. **Open WorkoutSessionScreen.kt:**
   - File should already exist from Phase 5/6
   - Current content has MuscleGroupCard with placeholder onAddExercise

2. **Add imports:**
   - Add: `import com.journal.ernie.ui.dialogs.AddExerciseDialog`
   - Add: `import androidx.compose.runtime.remember`

3. **Add dialog state:**
   - Add state for AddExerciseDialog:
     ```kotlin
     var showAddExerciseDialog by remember { mutableStateOf(false) }
     var selectedMuscleGroupId by remember { mutableStateOf<String?>(null) }
     ```
   - `showAddExerciseDialog` controls dialog visibility
   - `selectedMuscleGroupId` tracks which muscle group to add exercise to

4. **Update MuscleGroupCard call:**
   - Find the `MuscleGroupCard` call in the items block
   - Update `onAddExercise` callback:
     ```kotlin
     onAddExercise = {
         selectedMuscleGroupId = muscleGroup.id
         showAddExerciseDialog = true
     }
     ```
   - Update `onAddSet` callback:
     ```kotlin
     onAddSet = { exerciseId ->
         viewModel.addSet(muscleGroup.id, exerciseId)
     }
     ```
   - Update `onRemoveExercise` callback:
     ```kotlin
     onRemoveExercise = { exerciseId ->
         viewModel.removeExercise(muscleGroup.id, exerciseId)
     }
     ```

5. **Add AddExerciseDialog:**
   - After AddMuscleGroupDialog, add:
     ```kotlin
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
     ```

6. **Verify currentSession is not null:**
   - Ensure all ViewModel calls are within the `currentSession != null` check
   - This prevents crashes if session is null

### Expected Code Structure (WorkoutSessionScreen.kt after update):

```kotlin
// ... existing imports ...
import com.journal.ernie.ui.dialogs.AddExerciseDialog
import com.journal.ernie.ui.dialogs.AddMuscleGroupDialog

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
        // ... existing Scaffold code ...
    ) { paddingValues ->
        if (currentSession == null) {
            // ... existing null session handling ...
        } else {
            LazyColumn(
                // ... existing LazyColumn setup ...
            ) {
                // Timer display
                item {
                    TimerDisplay(
                        // ... existing timer code ...
                    )
                }
                
                // Muscle groups
                if (currentSession.muscleGroups.isEmpty()) {
                    // ... existing empty state ...
                } else {
                    items(currentSession.muscleGroups) { muscleGroup ->
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
```

### Important Notes:

- **Why selectedMuscleGroupId state?** Need to track which muscle group to add exercise to. Dialog doesn't know context.
- **Why null check?** Prevents adding exercise to null muscle group. Safety check.
- **Why clear state on dismiss?** Resets dialog state. Prevents stale data.
- **Why let block?** Safe call operator. Only calls ViewModel if groupId is not null.

### Testing Checklist:

After updating this file, verify:
- [ ] File compiles without errors
- [ ] Add Exercise button opens dialog
- [ ] Dialog displays correctly
- [ ] Adding exercise adds it to correct muscle group
- [ ] Exercise appears in ExerciseCard
- [ ] Remove Exercise button removes exercise
- [ ] Add Set button adds set to exercise
- [ ] Sets display in ExerciseCard
- [ ] Multiple exercises work correctly
- [ ] UI updates when exercises are added/removed

---

## Task 7.5: Test Exercise Management Flow

**Purpose:** Verify that the complete exercise management flow works correctly.

### Step-by-Step Testing:

1. **Test adding exercise:**
   - Navigate to WorkoutSessionScreen
   - Add a muscle group (if none exists)
   - Tap "Add Exercise" button in MuscleGroupCard
   - Verify dialog opens
   - Enter exercise name
   - Tap Add
   - Verify exercise appears in ExerciseCard

2. **Test removing exercise:**
   - Add an exercise
   - Tap delete icon on ExerciseCard
   - Verify exercise is removed

3. **Test adding set:**
   - Add an exercise
   - Tap "Add Set" button in ExerciseCard
   - Verify set is added (shows in set list)
   - Verify set has default values (0 reps, 0.0 weight)

4. **Test multiple exercises:**
   - Add multiple exercises to a muscle group
   - Verify all display correctly
   - Verify each can be removed independently

5. **Test multiple sets:**
   - Add multiple sets to an exercise
   - Verify all sets display
   - Verify set numbering is correct

6. **Test validation:**
   - Open AddExerciseDialog
   - Leave text field empty
   - Verify Add button is disabled
   - Type text
   - Verify Add button is enabled

### Expected Behavior:

- **Adding exercise:** User taps Add Exercise → Dialog opens → User enters name → Exercise appears in ExerciseCard
- **Removing exercise:** User taps delete → Exercise disappears
- **Adding set:** User taps Add Set → Set appears with default values
- **Multiple exercises:** All exercises display in muscle group
- **Multiple sets:** All sets display in exercise

### Testing Checklist:

- [ ] Adding exercise works
- [ ] Removing exercise works
- [ ] Adding set works
- [ ] Multiple exercises work
- [ ] Multiple sets work
- [ ] Validation works
- [ ] UI updates correctly
- [ ] No crashes or errors

---

## Phase 7 Complete: Final Verification

After completing all tasks, run these comprehensive tests:

### Exercise Management Test:
- [ ] Add Exercise button opens dialog
- [ ] Dialog displays correctly
- [ ] Adding exercise adds it to correct muscle group
- [ ] Exercise appears in ExerciseCard
- [ ] Remove Exercise button removes exercise
- [ ] Multiple exercises can be added
- [ ] Each exercise can be removed independently

### Set Management Test:
- [ ] Add Set button adds set to exercise
- [ ] Sets display in ExerciseCard
- [ ] Set shows reps, weight, and comment
- [ ] Multiple sets can be added
- [ ] Sets have default values (0 reps, 0.0 weight)

### UI/UX Test:
- [ ] ExerciseCard displays correctly
- [ ] Nested appearance is clear (ExerciseCard within MuscleGroupCard)
- [ ] Empty states show correctly
- [ ] Layout is scrollable
- [ ] Spacing and padding are consistent
- [ ] Buttons are clearly labeled

### Integration Test:
- [ ] ViewModel functions are called correctly
- [ ] State updates trigger UI recomposition
- [ ] Exercises persist in muscle groups
- [ ] Sets persist in exercises
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

### Issue: "Unresolved reference: ExerciseCard"
**Solution:** 
- Verify ExerciseCard.kt is in `ui/components/` package
- Check import: `import com.journal.ernie.ui.components.ExerciseCard`

### Issue: Exercise doesn't appear after adding
**Solution:**
- Verify ViewModel's `addExercise()` is being called with correct groupId
- Check that `collectAsState()` is used to observe currentSession
- Verify ViewModel is passed correctly to WorkoutSessionScreen
- Check logcat for any errors

### Issue: Remove button doesn't work
**Solution:**
- Verify `onRemoveExercise` callback is passed correctly
- Check that ViewModel's `removeExercise()` is being called with correct IDs
- Verify exercise ID is correct
- Check logcat for any errors

### Issue: Add Set button doesn't work
**Solution:**
- Verify `onAddSet` callback is passed correctly
- Check that ViewModel's `addSet()` is being called with correct IDs
- Verify groupId and exerciseId are correct
- Check logcat for any errors

### Issue: Sets don't display
**Solution:**
- Verify sets are being added to exercise
- Check that ExerciseCard is displaying sets correctly
- Verify set data (reps, weight, comment) is accessible
- Check logcat for any errors

### Issue: Dialog doesn't open
**Solution:**
- Check that `showAddExerciseDialog` state is managed correctly
- Verify `selectedMuscleGroupId` is set before opening dialog
- Check that dialog conditional is correct
- Verify dialog composable is called

### Issue: Exercise added to wrong muscle group
**Solution:**
- Verify `selectedMuscleGroupId` is set correctly
- Check that groupId is passed to ViewModel's `addExercise()`
- Verify muscle group ID matches selected group

---

## Architecture Notes

### Data Flow:

```
User Action (Tap Add Exercise)
    ↓
MuscleGroupCard (sets selectedMuscleGroupId, shows dialog)
    ↓
User Enters Name & Confirms
    ↓
AddExerciseDialog (calls onConfirm)
    ↓
WorkoutSessionScreen (calls viewModel.addExercise)
    ↓
WorkoutViewModel (updates StateFlow)
    ↓
StateFlow Emits New Value
    ↓
WorkoutSessionScreen (collectAsState recomposes)
    ↓
MuscleGroupCard (displays new ExerciseCard)
```

### Component Hierarchy:

```
WorkoutSessionScreen
    └── MuscleGroupCard (for each group)
        └── ExerciseCard (for each exercise)
            └── Sets display (basic, will be SetRow in Phase 8)
```

### State Management:

- **ViewModel:** Holds current session in StateFlow (already implemented in Phase 3)
- **UI:** Observes StateFlow with `collectAsState()`
- **Updates:** ViewModel functions modify StateFlow, UI automatically recomposes
- **Dialog State:** Managed locally in WorkoutSessionScreen

---

## Next Steps: Phase 8

After Phase 7 is complete and verified, you're ready for Phase 8:
- Create SetRow component for displaying/editing sets
- Implement add/remove/update set in ViewModel (already done in Phase 3)
- Wire up UI for set editing
- This will allow users to edit reps, weight, and comments for each set

**Phase 7 establishes exercise management. Phase 8 will add full set editing functionality.**

---

## Summary

Phase 7 creates:
- ✅ AddExerciseDialog for adding exercises
- ✅ ExerciseCard component for displaying exercises with sets
- ✅ MuscleGroupCard update to show ExerciseCard components
- ✅ WorkoutSessionScreen integration with exercise management
- ✅ Add/remove exercise functionality
- ✅ Add set functionality (basic display)

**Total Files Created:** 2 new files (AddExerciseDialog.kt, ExerciseCard.kt)
**Total Files Modified:** 2 files (MuscleGroupCard.kt, WorkoutSessionScreen.kt)
**Total Lines of Code:** ~400-500 lines
**Time Estimate:** 2-3 hours for careful implementation

The exercise management is now functional. Users can add exercises to muscle groups, remove exercises, and add sets to exercises. Sets are displayed with basic information. Phase 8 will add full set editing capabilities.

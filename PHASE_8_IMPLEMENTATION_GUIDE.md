# Phase 8: Set Management - Detailed Implementation Guide

## Overview

This document provides step-by-step instructions for implementing Phase 8 of the Workout Tracking App. Phase 8 adds full set management functionality, allowing users to edit reps, weight, and comments for each set. This includes creating a SetRow component for displaying and editing sets, and wiring it up with the ViewModel functions that were already implemented in Phase 3.

**Goal:** Enable users to fully edit sets (reps, weight, comments) within exercises. The ViewModel functions for this were already implemented in Phase 3, so this phase focuses on the UI components and wiring them up for a complete set editing experience.

**Prerequisites:** 
- Phase 1 must be completed (all data models exist, including SetEntry)
- Phase 3 must be completed (WorkoutViewModel with addSet/removeSet/updateSet functions)
- Phase 7 must be completed (ExerciseCard component exists)
- Understanding of Compose state management and text input
- Basic knowledge of Material3 components (TextField, Card, Button, IconButton)

---

## Phase 8 Overview

Phase 8 consists of 3 main tasks:
1. Create SetRow component for displaying and editing sets
2. Update ExerciseCard to use SetRow components
3. Wire up set editing functionality with ViewModel

**Estimated Time:** 2-3 hours
**Files to Create:** 1 new file
**Files to Modify:** 1 existing file

---

## Task 8.1: Create SetRow Component

**File Path:** `src/main/java/com/journal/ernie/ui/components/SetRow.kt`

**Purpose:** Create a reusable row component that displays a set's information (reps, weight, comment) and allows editing these values.

### Step-by-Step Instructions:

1. **Create the file (components folder should exist from previous phases):**
   - Right-click on `components` package folder (under `ui`)
   - Select "New" → "Kotlin Class/File"
   - Name: `SetRow`
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
   import androidx.compose.material.icons.filled.Delete
   import androidx.compose.material.icons.filled.Edit
   import androidx.compose.material3.Card
   import androidx.compose.material3.Icon
   import androidx.compose.material3.IconButton
   import androidx.compose.material3.MaterialTheme
   import androidx.compose.material3.OutlinedTextField
   import androidx.compose.material3.Text
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
   ```

4. **Create the composable function:**
   - Function signature:
   ```kotlin
   @Composable
   fun SetRow(
       set: SetEntry,
       setNumber: Int,
       onUpdate: (Int, Float, String?) -> Unit,  // reps, weight, comment
       onDelete: () -> Unit,
       modifier: Modifier = Modifier
   )
   ```
   - Parameters:
     - `set: SetEntry` - The set to display/edit
     - `setNumber: Int` - The set number (1, 2, 3, etc.) for display
     - `onUpdate: (Int, Float, String?) -> Unit` - Callback when set is updated (reps, weight, comment)
     - `onDelete: () -> Unit` - Callback when delete button is clicked
     - `modifier: Modifier` - For styling and layout

5. **Add edit mode state:**
   - `var isEditing by remember { mutableStateOf(false) }`
   - Controls whether set is in edit mode or display mode

6. **Add local state for editing:**
   - `var repsText by remember { mutableStateOf(set.reps.toString()) }`
   - `var weightText by remember { mutableStateOf(set.weight.toString()) }`
   - `var commentText by remember { mutableStateOf(set.comment ?: "") }`
   - These hold the text field values while editing

7. **Create two display modes:**
   - **Display mode:** Show set info as Text (read-only)
   - **Edit mode:** Show TextFields for editing

8. **Display mode layout:**
   - Row with:
     - Set number (e.g., "Set 1")
     - Reps display
     - Weight display
     - Comment display (if exists)
     - Edit button
     - Delete button

9. **Edit mode layout:**
   - Column with:
     - Row with set number and buttons (Save, Cancel, Delete)
     - TextField for reps
     - TextField for weight
     - TextField for comment (optional)

10. **Handle text input validation:**
    - Reps: Only allow integers (0 or positive)
    - Weight: Allow decimals (0 or positive)
    - Comment: Allow any text (optional)

11. **Handle save:**
    - Parse repsText to Int (handle invalid input)
    - Parse weightText to Float (handle invalid input)
    - Call `onUpdate(reps, weight, commentText.ifEmpty { null })`
    - Set `isEditing = false`

12. **Handle cancel:**
    - Reset text fields to original values
    - Set `isEditing = false`

13. **Handle delete:**
    - Call `onDelete()`
    - Set `isEditing = false` (if in edit mode)

14. **Style the component:**
    - Use Card or Row for container
    - Proper padding and spacing
    - Material3 theme colors
    - Visual distinction between display and edit mode

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
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
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
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
                            onUpdate(reps, weight, commentText.ifEmpty { null })
                            isEditing = false
                        }) {
                            Text("Save")
                        }
                        TextButton(onClick = {
                            // Cancel - reset to original values
                            repsText = set.reps.toString()
                            weightText = set.weight.toString()
                            commentText = set.comment ?: ""
                            isEditing = false
                        }) {
                            Text("Cancel")
                        }
                        IconButton(onClick = {
                            onDelete()
                            isEditing = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
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
                                repsText = it
                            }
                        },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { 
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                weightText = it
                            }
                        },
                        label = { Text("Weight") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
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
                    if (set.comment != null && set.comment.isNotEmpty()) {
                        Text(
                            text = set.comment,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                Row {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Set"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Set",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
```

### Important Notes:

- **Why two modes?** Display mode is cleaner for viewing, edit mode provides full editing. Better UX than always showing text fields.
- **Why local state?** Text fields need local state for smooth editing. Only update parent when saved.
- **Why validation?** Prevents invalid input (negative numbers, non-numeric). Better data quality.
- **Why toIntOrNull/toFloatOrNull?** Handles invalid input gracefully. Returns null if can't parse, use default value.
- **Why remember state?** Preserves edit state across recompositions. Better than losing input.
- **Why reset on cancel?** User expects cancel to discard changes. Reset to original values.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] Set displays correctly in display mode
- [ ] Edit button switches to edit mode
- [ ] Text fields show current values in edit mode
- [ ] Reps field only accepts integers
- [ ] Weight field only accepts numbers/decimals
- [ ] Comment field accepts any text
- [ ] Save button updates set
- [ ] Cancel button discards changes
- [ ] Delete button removes set
- [ ] Display mode shows updated values after save

---

## Task 8.2: Update ExerciseCard to Use SetRow Components

**File Path:** `src/main/java/com/journal/ernie/ui/components/ExerciseCard.kt`

**Purpose:** Replace the basic set display in ExerciseCard with SetRow components for full set editing.

### Step-by-Step Instructions:

1. **Open ExerciseCard.kt:**
   - File should already exist from Phase 7
   - Current content shows basic set info display

2. **Add imports:**
   - Add: `import com.journal.ernie.ui.components.SetRow`
   - Add: `import androidx.compose.foundation.layout.Arrangement`
   - Add: `import androidx.compose.foundation.layout.spacedBy`

3. **Update function signature:**
   - Add parameter for set callbacks:
     ```kotlin
     fun ExerciseCard(
         exercise: Exercise,
         onAddSet: () -> Unit,
         onRemoveExercise: () -> Unit,
         onUpdateSet: (Int, Int, Float, String?) -> Unit,  // setIndex, reps, weight, comment
         onRemoveSet: (Int) -> Unit,  // setIndex
         modifier: Modifier = Modifier
     )
     ```
   - New parameters:
     - `onUpdateSet: (Int, Int, Float, String?) -> Unit` - Callback with setIndex, reps, weight, comment
     - `onRemoveSet: (Int) -> Unit` - Callback with setIndex

4. **Replace basic set display with SetRow:**
   - Find the basic set info display section
   - Replace with:
     ```kotlin
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
     ```

5. **Remove old set display code:**
   - Remove the basic set info display code
   - Keep the set count text if desired, or remove it (SetRow shows set number)

### Expected Code Structure (ExerciseCard.kt after update):

```kotlin
package com.journal.ernie.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
                        imageVector = Icons.Default.Delete,
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

- **Why forEachIndexed?** Need index to pass to callbacks. SetRow needs setNumber for display.
- **Why index + 1 for setNumber?** Sets are numbered 1, 2, 3... but list indices are 0, 1, 2...
- **Why pass index to callbacks?** ViewModel needs setIndex to update/remove correct set.
- **Why spacedBy?** Provides consistent spacing between SetRow components.

### Testing Checklist:

After updating this file, verify:
- [ ] File compiles without errors
- [ ] SetRow components display for each set
- [ ] Set numbers are correct (1, 2, 3...)
- [ ] Empty state shows when no sets
- [ ] Sets are properly spaced
- [ ] Callbacks are passed correctly

---

## Task 8.3: Update WorkoutSessionScreen to Wire Up Set Editing

**File Path:** `src/main/java/com/journal/ernie/ui/WorkoutSessionScreen.kt`

**Purpose:** Wire up the set editing functionality by connecting ExerciseCard's set callbacks to ViewModel functions.

### Step-by-Step Instructions:

1. **Open WorkoutSessionScreen.kt:**
   - File should already exist from Phase 5/6/7
   - Current content has ExerciseCard with placeholder set callbacks

2. **Update ExerciseCard callbacks:**
   - Find the `ExerciseCard` call in MuscleGroupCard
   - Update `onUpdateSet` callback:
     ```kotlin
     onUpdateSet = { setIndex, reps, weight, comment ->
         viewModel.updateSet(muscleGroup.id, exercise.id, setIndex, reps, weight, comment)
     }
     ```
   - Update `onRemoveSet` callback:
     ```kotlin
     onRemoveSet = { setIndex ->
         viewModel.removeSet(muscleGroup.id, exercise.id, setIndex)
     }
     ```

3. **Verify ViewModel function signatures:**
   - Check that `updateSet` signature matches: `(groupId: String, exerciseId: String, setIndex: Int, reps: Int, weight: Float, comment: String?)`
   - Check that `removeSet` signature matches: `(groupId: String, exerciseId: String, setIndex: Int)`
   - These should already be correct from Phase 3

4. **Test the integration:**
   - Verify all callbacks are connected
   - Ensure ViewModel functions are called with correct parameters

### Expected Code Structure (WorkoutSessionScreen.kt after update):

```kotlin
// ... existing code ...

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
        },
        onUpdateSet = { setIndex, reps, weight, comment ->
            // Need to find exercise - this will be handled in MuscleGroupCard
            // Actually, we need to pass exerciseId too
        },
        onRemoveSet = { exerciseId, setIndex ->
            viewModel.removeSet(muscleGroup.id, exerciseId, setIndex)
        }
    )
}

// ... rest of code ...
```

Wait, there's an issue. The callbacks need exerciseId, but MuscleGroupCard doesn't have it in the callback signature. Let me fix this in the guide.

Actually, looking at the ExerciseCard signature, it already has the callbacks that include setIndex. The issue is that MuscleGroupCard needs to pass exerciseId to the callbacks. Let me update the guide to reflect this.

### Revised Approach:

The ExerciseCard already has the correct callbacks. We need to update MuscleGroupCard to pass exerciseId when calling ExerciseCard, and then WorkoutSessionScreen needs to pass the callbacks that include both groupId and exerciseId.

Let me revise Task 8.3:

### Revised Task 8.3: Update MuscleGroupCard and WorkoutSessionScreen

**Step 1: Update MuscleGroupCard to pass exerciseId in callbacks**

In MuscleGroupCard.kt, update the ExerciseCard call:

```kotlin
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
```

**Step 2: Update MuscleGroupCard function signature**

```kotlin
fun MuscleGroupCard(
    muscleGroup: MuscleGroup,
    onAddExercise: () -> Unit,
    onRemoveGroup: () -> Unit,
    onAddSet: (String) -> Unit,  // exerciseId
    onRemoveExercise: (String) -> Unit,  // exerciseId
    onUpdateSet: (String, Int, Int, Float, String?) -> Unit,  // exerciseId, setIndex, reps, weight, comment
    onRemoveSet: (String, Int) -> Unit,  // exerciseId, setIndex
    modifier: Modifier = Modifier
)
```

**Step 3: Update WorkoutSessionScreen to pass callbacks with groupId**

```kotlin
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
    },
    onUpdateSet = { exerciseId, setIndex, reps, weight, comment ->
        viewModel.updateSet(muscleGroup.id, exerciseId, setIndex, reps, weight, comment)
    },
    onRemoveSet = { exerciseId, setIndex ->
        viewModel.removeSet(muscleGroup.id, exerciseId, setIndex)
    }
)
```

### Expected Code Structure (MuscleGroupCard.kt after update):

```kotlin
// ... existing code ...

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

// ... rest of code ...
```

### Expected Code Structure (WorkoutSessionScreen.kt after update):

```kotlin
// ... existing code in items block ...

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
    },
    onUpdateSet = { exerciseId, setIndex, reps, weight, comment ->
        viewModel.updateSet(muscleGroup.id, exerciseId, setIndex, reps, weight, comment)
    },
    onRemoveSet = { exerciseId, setIndex ->
        viewModel.removeSet(muscleGroup.id, exerciseId, setIndex)
    }
)

// ... rest of code ...
```

### Important Notes:

- **Why pass exerciseId in callbacks?** ViewModel needs both groupId and exerciseId to find the correct exercise. ExerciseCard doesn't know its parent group.
- **Why pass setIndex?** ViewModel needs setIndex to update/remove the correct set in the exercise's sets list.
- **Why chain callbacks?** ExerciseCard → MuscleGroupCard → WorkoutSessionScreen → ViewModel. Each layer adds necessary context.

### Testing Checklist:

After updating these files, verify:
- [ ] Files compile without errors
- [ ] Set editing works (reps, weight, comment)
- [ ] Set deletion works
- [ ] Set updates persist
- [ ] Multiple sets can be edited
- [ ] UI updates when sets are modified
- [ ] No crashes or errors

---

## Task 8.4: Test Set Management Flow

**Purpose:** Verify that the complete set management flow works correctly.

### Step-by-Step Testing:

1. **Test adding set:**
   - Navigate to WorkoutSessionScreen
   - Add a muscle group and exercise (if none exist)
   - Tap "Add Set" button in ExerciseCard
   - Verify set is added with default values (0 reps, 0.0 weight)
   - Verify SetRow appears

2. **Test editing set:**
   - Tap Edit button on a set
   - Verify edit mode opens
   - Change reps, weight, and comment
   - Tap Save
   - Verify changes are saved and displayed

3. **Test canceling edit:**
   - Tap Edit button on a set
   - Make changes
   - Tap Cancel
   - Verify changes are discarded
   - Verify original values are displayed

4. **Test deleting set:**
   - Tap Delete button on a set
   - Verify set is removed
   - Verify set count updates

5. **Test input validation:**
   - Tap Edit on a set
   - Try to enter negative reps
   - Verify only positive numbers are accepted
   - Try to enter letters in weight field
   - Verify only numbers/decimals are accepted

6. **Test multiple sets:**
   - Add multiple sets
   - Edit each set independently
   - Verify all sets display correctly
   - Verify set numbers are correct (1, 2, 3...)

7. **Test comment field:**
   - Edit a set
   - Add a comment
   - Save
   - Verify comment displays
   - Edit again
   - Clear comment
   - Save
   - Verify comment is removed

### Expected Behavior:

- **Adding set:** User taps Add Set → Set appears with default values → User can edit
- **Editing set:** User taps Edit → Edit mode opens → User changes values → User taps Save → Changes are saved
- **Deleting set:** User taps Delete → Set is removed
- **Input validation:** Only valid numbers are accepted
- **Multiple sets:** All sets can be edited independently

### Testing Checklist:

- [ ] Adding set works
- [ ] Editing set works
- [ ] Saving changes works
- [ ] Canceling edit works
- [ ] Deleting set works
- [ ] Input validation works
- [ ] Multiple sets work
- [ ] Comments work
- [ ] UI updates correctly
- [ ] No crashes or errors

---

## Phase 8 Complete: Final Verification

After completing all tasks, run these comprehensive tests:

### Set Management Test:
- [ ] SetRow displays correctly in display mode
- [ ] Edit mode opens when Edit button is clicked
- [ ] Text fields show current values
- [ ] Reps field only accepts integers
- [ ] Weight field only accepts numbers/decimals
- [ ] Comment field accepts any text
- [ ] Save button updates set
- [ ] Cancel button discards changes
- [ ] Delete button removes set
- [ ] Display mode shows updated values

### Set CRUD Test:
- [ ] Adding set works
- [ ] Editing set works
- [ ] Updating set works (reps, weight, comment)
- [ ] Deleting set works
- [ ] Multiple sets work
- [ ] Set numbers are correct

### UI/UX Test:
- [ ] SetRow displays correctly
- [ ] Edit mode is clear and functional
- [ ] Display mode is clean and readable
- [ ] Buttons are clearly labeled
- [ ] Layout is properly spaced
- [ ] Visual hierarchy is clear

### Integration Test:
- [ ] ViewModel functions are called correctly
- [ ] State updates trigger UI recomposition
- [ ] Set changes persist
- [ ] No crashes or errors in logcat
- [ ] State persists across navigation

### Code Quality:
- [ ] All files compile without errors
- [ ] No unused imports
- [ ] Code follows Kotlin conventions
- [ ] Components are reusable
- [ ] Error handling is present
- [ ] Input validation is robust

---

## Common Issues & Solutions

### Issue: "Unresolved reference: SetRow"
**Solution:** 
- Verify SetRow.kt is in `ui/components/` package
- Check import: `import com.journal.ernie.ui.components.SetRow`

### Issue: Set doesn't update after saving
**Solution:**
- Verify `onUpdate` callback is called with correct parameters
- Check that ViewModel's `updateSet()` is being called
- Verify setIndex, reps, weight, and comment are passed correctly
- Check logcat for any errors

### Issue: Invalid input accepted
**Solution:**
- Check input validation in SetRow
- Verify regex patterns for reps and weight
- Ensure toIntOrNull/toFloatOrNull are used
- Add additional validation if needed

### Issue: Edit mode doesn't open
**Solution:**
- Check that `isEditing` state is managed correctly
- Verify Edit button onClick handler
- Check that state is remembered

### Issue: Changes lost on cancel
**Solution:**
- This is expected behavior
- Verify that original values are restored
- Check that state is reset correctly

### Issue: Set deleted but still visible
**Solution:**
- Verify `onDelete` callback is called
- Check that ViewModel's `removeSet()` is being called
- Verify StateFlow updates trigger recomposition
- Check logcat for any errors

### Issue: Set numbers incorrect
**Solution:**
- Verify `setNumber = index + 1` in forEachIndexed
- Check that sets list is not modified incorrectly
- Verify index is correct

---

## Architecture Notes

### Data Flow:

```
User Action (Edit Set)
    ↓
SetRow (enters edit mode, user changes values)
    ↓
User Taps Save
    ↓
SetRow (calls onUpdate with new values)
    ↓
ExerciseCard (calls onUpdateSet with exerciseId and setIndex)
    ↓
MuscleGroupCard (calls onUpdateSet with exerciseId and setIndex)
    ↓
WorkoutSessionScreen (calls viewModel.updateSet with groupId, exerciseId, setIndex)
    ↓
WorkoutViewModel (updates StateFlow)
    ↓
StateFlow Emits New Value
    ↓
WorkoutSessionScreen (collectAsState recomposes)
    ↓
SetRow (displays updated values)
```

### Component Hierarchy:

```
WorkoutSessionScreen
    └── MuscleGroupCard
        └── ExerciseCard
            └── SetRow (for each set)
                ├── Display Mode
                └── Edit Mode
```

### State Management:

- **ViewModel:** Holds current session in StateFlow (already implemented in Phase 3)
- **UI:** Observes StateFlow with `collectAsState()`
- **Updates:** ViewModel functions modify StateFlow, UI automatically recomposes
- **Local State:** SetRow manages its own edit state for smooth editing experience

---

## Next Steps: Phase 9

After Phase 8 is complete and verified, you're ready for Phase 9:
- Add empty states throughout the app
- Add validation (e.g., prevent empty names)
- Test full workflow end-to-end
- Prepare for Room database integration (add TODO comments)
- Polish UI/UX

**Phase 8 completes the core functionality. Phase 9 will polish and prepare for persistence.**

---

## Summary

Phase 8 creates:
- ✅ SetRow component for displaying and editing sets
- ✅ ExerciseCard update to use SetRow components
- ✅ MuscleGroupCard update to pass set callbacks
- ✅ WorkoutSessionScreen integration with set editing
- ✅ Full set CRUD functionality (create, read, update, delete)

**Total Files Created:** 1 new file (SetRow.kt)
**Total Files Modified:** 2 files (ExerciseCard.kt, MuscleGroupCard.kt, WorkoutSessionScreen.kt)
**Total Lines of Code:** ~400-500 lines
**Time Estimate:** 2-3 hours for careful implementation

The set management is now fully functional. Users can add sets, edit reps/weight/comments, and delete sets. The complete workout tracking functionality is now in place. Phase 9 will add polish and prepare for database integration.

# Phase 5: Workout Session Screen - Basic - Detailed Implementation Guide

## Overview

This document provides step-by-step instructions for implementing Phase 5 of the Workout Tracking App. Phase 5 creates the basic WorkoutSessionScreen layout with a timer display and shows muscle groups from the current workout session. This is the main screen where users track their workouts.

**Goal:** Create the WorkoutSessionScreen that displays the timer, shows muscle groups from the current session, and provides the foundation for exercise and set tracking in later phases.

**Prerequisites:** 
- Phase 1 must be completed (all data models exist)
- Phase 2 must be completed (HomeScreen and navigation exist)
- Phase 3 must be completed (WorkoutViewModel exists)
- Phase 4 must be completed (WorkoutListScreen exists)
- Understanding of Compose StateFlow collection
- Basic knowledge of Material3 components (Scaffold, Card, Button, LazyColumn)

---

## Phase 5 Overview

Phase 5 consists of 3 main tasks:
1. Create TimerDisplay component for displaying and controlling the workout timer
2. Create basic WorkoutSessionScreen layout with timer and muscle group display
3. Update AppNavigation to use WorkoutSessionScreen

**Estimated Time:** 2-3 hours
**Files to Create:** 2 new files
**Files to Modify:** 1 existing file

---

## Task 5.1: Create TimerDisplay Component

**File Path:** `src/main/java/com/journal/ernie/ui/components/TimerDisplay.kt`

**Purpose:** Create a reusable timer component that displays elapsed time and provides start/pause/reset controls.

### Step-by-Step Instructions:

1. **Create the file (if components folder doesn't exist, it should from Phase 4):**
   - Right-click on `components` package folder (under `ui`)
   - Select "New" → "Kotlin Class/File"
   - Name: `TimerDisplay`
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
   import androidx.compose.material3.Button
   import androidx.compose.material3.Card
   import androidx.compose.material3.CardDefaults
   import androidx.compose.material3.Icon
   import androidx.compose.material3.IconButton
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
   import androidx.compose.ui.text.font.FontWeight
   import androidx.compose.ui.unit.dp
   import androidx.compose.material.icons.Icons
   import androidx.compose.material.icons.filled.Pause
   import androidx.compose.material.icons.filled.PlayArrow
   import androidx.compose.material.icons.filled.Refresh
   import kotlinx.coroutines.delay
   ```

4. **Create the composable function:**
   - Function signature:
   ```kotlin
   @Composable
   fun TimerDisplay(
       elapsedTimeSeconds: Long,
       isRunning: Boolean,
       onStart: () -> Unit,
       onPause: () -> Unit,
       onReset: () -> Unit,
       modifier: Modifier = Modifier
   )
   ```
   - Parameters:
     - `elapsedTimeSeconds: Long` - Current elapsed time in seconds
     - `isRunning: Boolean` - Whether timer is currently running
     - `onStart: () -> Unit` - Callback when start button is clicked
     - `onPause: () -> Unit` - Callback when pause button is clicked
     - `onReset: () -> Unit` - Callback when reset button is clicked
     - `modifier: Modifier` - For styling and layout

5. **Format time display:**
   - Convert seconds to MM:SS or HH:MM:SS format
   - Calculate hours: `val hours = elapsedTimeSeconds / 3600`
   - Calculate minutes: `val minutes = (elapsedTimeSeconds % 3600) / 60`
   - Calculate seconds: `val seconds = elapsedTimeSeconds % 60`
   - Format as string: "HH:MM:SS" if hours > 0, else "MM:SS"

6. **Add local state for display update:**
   - `var displayTime by remember { mutableStateOf(elapsedTimeSeconds) }`
   - This will be updated by LaunchedEffect to show smooth counting

7. **Add LaunchedEffect for timer animation:**
   - Use `LaunchedEffect(isRunning, elapsedTimeSeconds)` to update display
   - When running, increment displayTime every second for smooth animation
   - When paused or reset, sync displayTime with elapsedTimeSeconds

8. **Create Card layout:**
   - Use `Card` component for container
   - Inside Card, use `Column` for vertical layout
   - Display:
     - Time display (large, bold, centered)
     - Row with three buttons: Start, Pause, Reset

9. **Implement buttons:**
   - Start button: Only visible when `!isRunning`, calls `onStart()`
   - Pause button: Only visible when `isRunning`, calls `onPause()`
   - Reset button: Always visible, calls `onReset()`
   - Use IconButton or Button with icons

10. **Style the component:**
    - Time display: Large font, bold, monospace (for consistent width)
    - Buttons: Material3 styling
    - Card: Elevation and padding

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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
                            imageVector = Icons.Default.PlayArrow,
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
                            imageVector = Icons.Default.Pause,
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
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Reset")
                }
            }
        }
    }
}
```

### Important Notes:

- **Why local displayTime state?** Provides smooth animation between ViewModel updates. ViewModel updates every second, but we can show smoother transitions.
- **Why LaunchedEffect?** Updates display time continuously when running. Provides smooth counting animation.
- **Why monospace font?** Keeps time display width consistent. Prevents layout shifts as numbers change.
- **Why conditional button display?** Shows Start when paused, Pause when running. Clearer UX than always showing both.
- **Why separate callbacks?** Allows ViewModel to handle timer logic. Component is purely presentational.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] Time displays in correct format (MM:SS or HH:MM:SS)
- [ ] Start button appears when not running
- [ ] Pause button appears when running
- [ ] Reset button is always visible
- [ ] Buttons call correct callbacks
- [ ] Time updates smoothly when running
- [ ] Time syncs correctly when paused

---

## Task 5.2: Create Basic WorkoutSessionScreen Layout

**File Path:** `src/main/java/com/journal/ernie/ui/WorkoutSessionScreen.kt`

**Purpose:** Create the main workout session screen that displays the timer and shows muscle groups from the current session.

### Step-by-Step Instructions:

1. **Create the file:**
   - Right-click on `ui` package folder
   - Select "New" → "Kotlin Class/File"
   - Name: `WorkoutSessionScreen`
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
   import androidx.compose.material.icons.Icons
   import androidx.compose.material.icons.filled.ArrowBack
   import androidx.compose.material3.Card
   import androidx.compose.material3.Icon
   import androidx.compose.material3.IconButton
   import androidx.compose.material3.MaterialTheme
   import androidx.compose.material3.Scaffold
   import androidx.compose.material3.Text
   import androidx.compose.material3.TopAppBar
   import androidx.compose.runtime.Composable
   import androidx.compose.runtime.collectAsState
   import androidx.compose.runtime.getValue
   import androidx.compose.ui.Alignment
   import androidx.compose.ui.Modifier
   import androidx.compose.ui.text.font.FontWeight
   import androidx.compose.ui.unit.dp
   import com.journal.ernie.data.MuscleGroup
   import com.journal.ernie.data.WorkoutSession
   import com.journal.ernie.ui.components.TimerDisplay
   import com.journal.ernie.viewmodel.WorkoutViewModel
   ```

4. **Create the composable function:**
   - Function signature:
   ```kotlin
   @Composable
   fun WorkoutSessionScreen(
       viewModel: WorkoutViewModel,
       onNavigateBack: () -> Unit
   )
   ```
   - Parameters:
     - `viewModel: WorkoutViewModel` - The ViewModel for workout data
     - `onNavigateBack: () -> Unit` - Back navigation callback

5. **Collect StateFlow values:**
   - Collect current session: `val currentSession by viewModel.currentSession.collectAsState()`
   - Collect timer state: `val timerState by viewModel.timerState.collectAsState()`
   - These observe ViewModel state and recompose when changed

6. **Handle null session:**
   - If `currentSession == null`, show message:
     - "No active workout session"
     - "Please select a session from the list"
   - Return early or show empty state

7. **Create Scaffold layout:**
   - Use `Scaffold` component for Material3 structure
   - Add TopAppBar with:
     - Title: Session name (from `currentSession.name`)
     - Back button (IconButton with ArrowBack icon)
   - Add content area for timer and muscle groups

8. **Add TimerDisplay:**
   - Place at top of content (after TopAppBar)
   - Pass timer state: `elapsedTimeSeconds = timerState.elapsedTimeSeconds`
   - Pass `isRunning = timerState.isRunning`
   - Pass callbacks:
     - `onStart = { viewModel.startTimer() }`
     - `onPause = { viewModel.pauseTimer() }`
     - `onReset = { viewModel.resetTimer() }`

9. **Display muscle groups:**
   - Use `LazyColumn` for scrollable list
   - Get muscle groups: `val muscleGroups = currentSession.muscleGroups`
   - Use `items(muscleGroups) { group -> }` to iterate
   - For each group, display a simple Card with:
     - Muscle group name (large, bold)
     - Exercise count: "X exercises" (if any)
     - Placeholder text: "Exercises will be shown here" (for now)

10. **Handle empty muscle groups:**
    - If `muscleGroups.isEmpty()`, show message:
      - "No muscle groups yet"
      - "Add a muscle group to start tracking exercises"
    - Center the message

11. **Add padding and spacing:**
    - Add padding around content
    - Space between timer and muscle groups
    - Space between muscle group cards

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.journal.ernie.data.MuscleGroup
import com.journal.ernie.ui.components.TimerDisplay
import com.journal.ernie.viewmodel.WorkoutViewModel

@Composable
fun WorkoutSessionScreen(
    viewModel: WorkoutViewModel,
    onNavigateBack: () -> Unit
) {
    // Collect state from ViewModel
    val currentSession by viewModel.currentSession.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    
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
                                    text = "Add a muscle group to start tracking exercises",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(currentSession.muscleGroups) { muscleGroup ->
                        MuscleGroupCardBasic(muscleGroup = muscleGroup)
                    }
                }
            }
        }
    }
}

// Basic muscle group card (will be enhanced in Phase 6)
@Composable
fun MuscleGroupCardBasic(muscleGroup: MuscleGroup) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = muscleGroup.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (muscleGroup.exercises.isNotEmpty()) {
                Text(
                    text = "${muscleGroup.exercises.size} exercise${if (muscleGroup.exercises.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                Text(
                    text = "No exercises yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
```

### Important Notes:

- **Why check for null session?** User might navigate to screen without selecting a session. Handle gracefully.
- **Why collectAsState()?** Observes ViewModel StateFlow. Triggers recomposition when state changes.
- **Why LazyColumn?** Efficiently displays scrollable list. Only renders visible items.
- **Why basic MuscleGroupCard?** Phase 6 will create full MuscleGroupCard with exercises. This is a placeholder.
- **Why Scaffold?** Provides Material3 structure (TopAppBar, content area). Handles padding automatically.
- **Why empty state?** Better UX than blank screen. Guides user to add muscle groups.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] Screen displays when session is selected
- [ ] TopAppBar shows session name
- [ ] Back button navigates back
- [ ] TimerDisplay is visible and functional
- [ ] Timer buttons work (start, pause, reset)
- [ ] Muscle groups display in list
- [ ] Empty state shows when no muscle groups
- [ ] Null session state shows appropriate message

---

## Task 5.3: Update AppNavigation to Use WorkoutSessionScreen

**File Path:** `src/main/java/com/journal/ernie/ui/AppNavigation.kt`

**Purpose:** Replace the placeholder WorkoutSessionScreen with the actual implementation.

### Step-by-Step Instructions:

1. **Open AppNavigation.kt:**
   - File should already exist from Phase 4
   - Current content has placeholder for WorkoutSessionScreen

2. **Add WorkoutSessionScreen import:**
   - Add: `import com.journal.ernie.ui.WorkoutSessionScreen`
   - Since it's in the same package, might not need explicit import, but good practice

3. **Replace WorkoutSessionScreen placeholder:**
   - Find `is Screen.WorkoutSession ->` case
   - Replace placeholder with:
     ```kotlin
     WorkoutSessionScreen(
         viewModel = viewModel,
         onNavigateBack = { onNavigateTo(Screen.WorkoutList) }
     )
     ```

4. **Verify other screens:**
   - HomeScreen should already be implemented from Phase 2
   - WorkoutListScreen should already be implemented from Phase 4

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
            WorkoutSessionScreen(
                viewModel = viewModel,
                onNavigateBack = { onNavigateTo(Screen.WorkoutList) }
            )
        }
    }
}
```

### Important Notes:

- **Why pass ViewModel?** WorkoutSessionScreen needs ViewModel to access current session and timer state.
- **Why onNavigateBack?** Provides way to go back to WorkoutListScreen. Could also use system back button.
- **Why keep other screens?** All screens should now be implemented (Home, WorkoutList, WorkoutSession).

### Testing Checklist:

After updating AppNavigation, verify:
- [ ] File compiles without errors
- [ ] Navigation from WorkoutList to WorkoutSession works
- [ ] WorkoutSessionScreen displays correctly
- [ ] Back navigation works
- [ ] ViewModel is passed correctly

---

## Phase 5 Complete: Final Verification

After completing all tasks, run these comprehensive tests:

### Navigation Flow Test:
- [ ] Home → Workout → WorkoutListScreen displays
- [ ] WorkoutListScreen → Create session → WorkoutSessionScreen displays
- [ ] WorkoutListScreen → Select session → WorkoutSessionScreen displays
- [ ] WorkoutSessionScreen → Back button → Returns to WorkoutListScreen

### Timer Functionality Test:
- [ ] Timer displays correct initial time (00:00)
- [ ] Start button starts timer
- [ ] Timer increments every second when running
- [ ] Pause button pauses timer
- [ ] Timer stops incrementing when paused
- [ ] Reset button resets timer to 00:00
- [ ] Timer state persists across screen rotations (if implemented)

### Session Display Test:
- [ ] Session name displays in TopAppBar
- [ ] Muscle groups display in list
- [ ] Muscle group names are correct
- [ ] Exercise counts display correctly
- [ ] Empty state shows when no muscle groups
- [ ] Null session state shows appropriate message

### UI/UX Test:
- [ ] TopAppBar displays session name
- [ ] Back button is visible and functional
- [ ] TimerDisplay is properly styled
- [ ] Timer buttons are clearly labeled
- [ ] Muscle group cards have proper styling
- [ ] Layout is scrollable
- [ ] Spacing and padding are consistent

### Integration Test:
- [ ] ViewModel state is observed correctly
- [ ] Timer state updates trigger UI recomposition
- [ ] Session data displays correctly
- [ ] No crashes or errors in logcat
- [ ] State persists across navigation

### Code Quality:
- [ ] All files compile without errors
- [ ] No unused imports
- [ ] Code follows Kotlin conventions
- [ ] Components are reusable where appropriate
- [ ] Error handling is present (null checks, empty states)

---

## Common Issues & Solutions

### Issue: "Unresolved reference: TimerDisplay"
**Solution:** 
- Verify TimerDisplay.kt is in `ui/components/` package
- Check import statement: `import com.journal.ernie.ui.components.TimerDisplay`

### Issue: Timer doesn't update smoothly
**Solution:**
- Check that LaunchedEffect is properly implemented
- Verify delay(1000) is used correctly
- Ensure displayTime state is being updated

### Issue: "Cannot find symbol: collectAsState"
**Solution:** 
- Add import: `import androidx.compose.runtime.collectAsState`
- Ensure compose-runtime dependency is in build.gradle

### Issue: Timer buttons don't work
**Solution:**
- Verify callbacks are passed correctly to TimerDisplay
- Check that ViewModel functions (startTimer, pauseTimer, resetTimer) exist
- Verify ViewModel is passed correctly to WorkoutSessionScreen

### Issue: Muscle groups don't display
**Solution:**
- Verify currentSession is not null
- Check that muscleGroups list is not empty
- Verify collectAsState() is used to observe currentSession
- Check logcat for any errors

### Issue: Session name doesn't display
**Solution:**
- Verify currentSession?.name is accessed correctly
- Check that session was selected before navigating
- Verify ViewModel's selectSession() was called

### Issue: Time format is wrong
**Solution:**
- Check String.format() usage
- Verify hours/minutes/seconds calculations
- Test with different elapsedTimeSeconds values

### Issue: LaunchedEffect causes recomposition loop
**Solution:**
- Ensure LaunchedEffect keys are correct (isRunning, elapsedTimeSeconds)
- Check that delay() is used to prevent infinite loops
- Verify state updates don't trigger unnecessary recompositions

---

## Architecture Notes

### Data Flow:

```
User Action (Start Timer)
    ↓
TimerDisplay (calls onStart)
    ↓
WorkoutSessionScreen (calls viewModel.startTimer())
    ↓
WorkoutViewModel (updates _timerState)
    ↓
StateFlow Emits New Value
    ↓
WorkoutSessionScreen (collectAsState recomposes)
    ↓
TimerDisplay (receives new elapsedTimeSeconds)
    ↓
UI Updates with New Time
```

### Component Hierarchy:

```
WorkoutSessionScreen
    ├── Scaffold
    │   ├── TopAppBar
    │   └── LazyColumn
    │       ├── TimerDisplay
    │       └── MuscleGroupCardBasic (for each group)
    └── Empty States (conditional)
```

### State Management:

- **ViewModel:** Holds current session and timer state in StateFlow
- **UI:** Observes StateFlow with `collectAsState()`
- **Updates:** ViewModel functions modify StateFlow, UI automatically recomposes
- **Timer:** ViewModel manages timer logic, UI displays state

---

## Next Steps: Phase 6

After Phase 5 is complete and verified, you're ready for Phase 6:
- Create MuscleGroupCard component with full functionality
- Create AddMuscleGroupDialog
- Implement add/remove muscle group in ViewModel (already done in Phase 3)
- Wire up UI for muscle group management
- This will allow users to add and manage muscle groups

**Phase 5 establishes the basic workout session screen. Phase 6 will add muscle group management functionality.**

---

## Summary

Phase 5 creates:
- ✅ TimerDisplay component for workout timer
- ✅ Basic WorkoutSessionScreen layout
- ✅ Muscle group display (basic cards)
- ✅ AppNavigation integration
- ✅ Timer integration with ViewModel

**Total Files Created:** 2 new files (TimerDisplay.kt, WorkoutSessionScreen.kt)
**Total Files Modified:** 1 file (AppNavigation.kt)
**Total Lines of Code:** ~300-400 lines
**Time Estimate:** 2-3 hours for careful implementation

The workout session screen is now functional with timer display and basic muscle group listing. Users can start workouts, see their sessions, and view muscle groups. Phase 6 will add the ability to manage muscle groups and exercises.

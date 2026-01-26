# Phase 3: ViewModel - Detailed Implementation Guide

## Overview

This document provides step-by-step instructions for implementing Phase 3 of the Workout Tracking App. Phase 3 creates the WorkoutViewModel that manages all workout-related state and business logic, including session management, CRUD operations for muscle groups/exercises/sets, and timer functionality.

**Goal:** Create a ViewModel that manages workout state using StateFlow, implements all CRUD operations, and provides timer functionality. This ViewModel will serve as the single source of truth for workout data.

**Prerequisites:** 
- Phase 1 must be completed (all data models exist)
- Phase 2 must be completed (HomeScreen and navigation exist)
- Understanding of ViewModel, StateFlow, and coroutines
- Basic knowledge of Kotlin coroutines (viewModelScope, launch)

---

## Phase 3 Overview

Phase 3 consists of 3 main tasks:
1. Create TimerState data class for timer state management
2. Create WorkoutViewModel with state management setup
3. Implement all business logic functions (session management, CRUD operations, timer)

**Estimated Time:** 3-4 hours
**Files to Create:** 2 new files
**Files to Modify:** 0 files (but will be used by Phase 4+)

---

## Task 3.1: Create TimerState Data Class

**File Path:** `src/main/java/com/journal/ernie/viewmodel/TimerState.kt`

**Purpose:** Create a data class to hold the timer's state (elapsed time and running status). This will be used by the ViewModel to manage timer state.

### Step-by-Step Instructions:

1. **Create the viewmodel package directory (if not exists):**
   - Right-click on `com/journal/ernie/` package
   - Select "New" → "Package"
   - Name: `viewmodel`

2. **Create the file:**
   - Right-click on `viewmodel` package folder
   - Select "New" → "Kotlin Class/File"
   - Name: `TimerState`
   - Type: "Data Class"

3. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.viewmodel
   ```

4. **Create the data class with properties:**
   - `elapsedTimeSeconds: Long` - Total elapsed time in seconds
     - Type: `Long`
     - Default: `0L`
     - Represents how many seconds have passed since timer started
   - `isRunning: Boolean` - Whether the timer is currently running
     - Type: `Boolean`
     - Default: `false`
     - True when timer is counting, false when paused or stopped

5. **Make it a data class:**
   - Use `data class` keyword (not regular `class`)
   - This provides automatic `equals()`, `hashCode()`, `toString()`, and `copy()` methods

6. **Add companion object with default factory (optional but recommended):**
   - Create `companion object { }` block
   - Add function: `fun initial(): TimerState`
   - Returns: `TimerState(elapsedTimeSeconds = 0L, isRunning = false)`
   - This provides a clear way to create initial timer state

### Expected Code Structure:

```kotlin
package com.journal.ernie.viewmodel

data class TimerState(
    val elapsedTimeSeconds: Long = 0L,
    val isRunning: Boolean = false
) {
    companion object {
        fun initial(): TimerState {
            return TimerState(
                elapsedTimeSeconds = 0L,
                isRunning = false
            )
        }
    }
}
```

### Important Notes:

- **Why data class?** TimerState is a simple container for two values. Data class provides value semantics and useful methods automatically.
- **Why Long for elapsedTimeSeconds?** Long can hold very large values (millions of seconds = days of workout time). Int would overflow after ~68 years, but Long is safer.
- **Why separate TimerState?** Separating timer state makes it easier to manage and test. It can be observed independently from workout session data.
- **Why companion object factory?** Provides a clear, named way to create initial state. Makes code more readable than `TimerState()`.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] You can create a TimerState: `TimerState()`
- [ ] You can create with values: `TimerState(elapsedTimeSeconds = 60L, isRunning = true)`
- [ ] Factory function works: `TimerState.initial()` returns state with 0 seconds and not running
- [ ] Copy method works: `timerState.copy(isRunning = true)` creates new state with updated value

---

## Task 3.2: Create WorkoutViewModel Class Structure

**File Path:** `src/main/java/com/journal/ernie/viewmodel/WorkoutViewModel.kt`

**Purpose:** Create the ViewModel class that will manage all workout-related state and business logic. This is the core of Phase 3.

### Step-by-Step Instructions:

1. **Create the file:**
   - Right-click on `viewmodel` package folder
   - Select "New" → "Kotlin Class/File"
   - Name: `WorkoutViewModel`
   - Type: "Class"

2. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.viewmodel
   ```

3. **Add all required imports:**
   ```kotlin
   import androidx.lifecycle.ViewModel
   import androidx.lifecycle.viewModelScope
   import com.journal.ernie.data.Exercise
   import com.journal.ernie.data.MuscleGroup
   import com.journal.ernie.data.SetEntry
   import com.journal.ernie.data.WorkoutSession
   import kotlinx.coroutines.flow.MutableStateFlow
   import kotlinx.coroutines.flow.StateFlow
   import kotlinx.coroutines.flow.asStateFlow
   import kotlinx.coroutines.flow.update
   import kotlinx.coroutines.launch
   ```

4. **Create the class declaration:**
   - Extend `ViewModel` (from `androidx.lifecycle.ViewModel`)
   - Class signature: `class WorkoutViewModel : ViewModel() { }`

5. **Add private mutable state flows:**
   - These will hold the actual state that can be modified
   - `private val _currentSession = MutableStateFlow<WorkoutSession?>(null)`
   - `private val _allSessions = MutableStateFlow<List<WorkoutSession>>(emptyList())`
   - `private val _timerState = MutableStateFlow<TimerState>(TimerState.initial())`
   - Use `MutableStateFlow` for internal state management

6. **Add public read-only state flows:**
   - These expose state to UI without allowing direct modification
   - `val currentSession: StateFlow<WorkoutSession?> = _currentSession.asStateFlow()`
   - `val allSessions: StateFlow<List<WorkoutSession>> = _allSessions.asStateFlow()`
   - `val timerState: StateFlow<TimerState> = _timerState.asStateFlow()`
   - Use `.asStateFlow()` to convert MutableStateFlow to read-only StateFlow

7. **Add init block (optional but recommended):**
   - `init { }` block to initialize ViewModel
   - Call `loadSessions()` to load any existing sessions
   - This ensures data is loaded when ViewModel is created

### Expected Code Structure (Initial Setup):

```kotlin
package com.journal.ernie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.journal.ernie.data.Exercise
import com.journal.ernie.data.MuscleGroup
import com.journal.ernie.data.SetEntry
import com.journal.ernie.data.WorkoutSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutViewModel : ViewModel() {
    // Private mutable state flows
    private val _currentSession = MutableStateFlow<WorkoutSession?>(null)
    private val _allSessions = MutableStateFlow<List<WorkoutSession>>(emptyList())
    private val _timerState = MutableStateFlow<TimerState>(TimerState.initial())
    
    // Public read-only state flows
    val currentSession: StateFlow<WorkoutSession?> = _currentSession.asStateFlow()
    val allSessions: StateFlow<List<WorkoutSession>> = _allSessions.asStateFlow()
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    
    init {
        loadSessions()
    }
    
    // Functions will be added in next tasks
}
```

### Important Notes:

- **Why ViewModel?** ViewModel survives configuration changes (screen rotation) and provides lifecycle-aware state management.
- **Why StateFlow?** StateFlow is a hot flow that holds current state and emits it to collectors. Perfect for UI state management in Compose.
- **Why private _currentSession and public currentSession?** This pattern prevents external code from directly modifying state. Only ViewModel can update state through its functions.
- **Why asStateFlow()?** Converts MutableStateFlow to read-only StateFlow. UI can observe but not modify directly.
- **Why viewModelScope?** Provides a coroutine scope tied to ViewModel lifecycle. Coroutines are cancelled when ViewModel is cleared.
- **Why init block?** Ensures data is loaded when ViewModel is created, so UI always has initial state.

### Testing Checklist:

After creating the class structure, verify:
- [ ] File compiles without errors
- [ ] ViewModel extends ViewModel correctly
- [ ] All StateFlow properties are accessible
- [ ] Initial state is correct (currentSession = null, allSessions = empty, timerState = initial)

---

## Task 3.3: Implement Session Management Functions

**File Path:** `src/main/java/com/journal/ernie/viewmodel/WorkoutViewModel.kt`

**Purpose:** Implement functions to create, load, and select workout sessions.

### Step-by-Step Instructions:

1. **Implement `loadSessions()` function:**
   - Function signature: `fun loadSessions() { }`
   - For now, this will be empty or load from memory
   - Later, this will load from Room database
   - Add TODO comment: `// TODO: Load from Room database when implemented`
   - For now, just initialize with empty list or leave as-is
   - Use `viewModelScope.launch { }` if you need to do async work later

2. **Implement `createNewSession(name: String)` function:**
   - Function signature: `fun createNewSession(name: String) { }`
   - Use `viewModelScope.launch { }` to run in coroutine
   - Create new session using factory: `val newSession = WorkoutSession.createNew(name)`
   - Update `_allSessions` using `.update { }`:
     ```kotlin
     _allSessions.update { currentList ->
         currentList + newSession
     }
     ```
   - Set as current session: `_currentSession.value = newSession`
   - Reset timer: `_timerState.value = TimerState.initial()`
   - Add TODO: `// TODO: Save to Room database when implemented`

3. **Implement `selectSession(id: String)` function:**
   - Function signature: `fun selectSession(id: String) { }`
   - Use `viewModelScope.launch { }` to run in coroutine
   - Find session in allSessions: `val session = _allSessions.value.find { it.id == id }`
   - If session found: `_currentSession.value = session`
   - If not found: `_currentSession.value = null` (or handle error)
   - Reset timer: `_timerState.value = TimerState.initial()`
   - Add TODO: `// TODO: Load full session data from Room database when implemented`

4. **Add helper function: `clearCurrentSession()` (optional but recommended):**
   - Function signature: `fun clearCurrentSession() { }`
   - Sets `_currentSession.value = null`
   - Resets timer: `_timerState.value = TimerState.initial()`
   - Useful when navigating away from workout session

### Expected Code Structure:

```kotlin
// Inside WorkoutViewModel class

fun loadSessions() {
    // TODO: Load from Room database when implemented
    // For now, sessions are stored in memory only
    // This function is called in init block
}

fun createNewSession(name: String) {
    viewModelScope.launch {
        val newSession = WorkoutSession.createNew(name)
        
        // Add to all sessions list
        _allSessions.update { currentList ->
            currentList + newSession
        }
        
        // Set as current session
        _currentSession.value = newSession
        
        // Reset timer
        _timerState.value = TimerState.initial()
        
        // TODO: Save to Room database when implemented
    }
}

fun selectSession(id: String) {
    viewModelScope.launch {
        val session = _allSessions.value.find { it.id == id }
        
        if (session != null) {
            _currentSession.value = session
            _timerState.value = TimerState.initial()
        } else {
            // Session not found - could log error or handle gracefully
            _currentSession.value = null
        }
        
        // TODO: Load full session data from Room database when implemented
    }
}

fun clearCurrentSession() {
    _currentSession.value = null
    _timerState.value = TimerState.initial()
}
```

### Important Notes:

- **Why viewModelScope.launch?** Even though we're not doing async work now, we will when Room is added. It's good practice to use coroutines from the start.
- **Why .update { }?** This is a safe way to update StateFlow. It provides the current value and returns the new value. Prevents race conditions.
- **Why reset timer on session change?** Each session should start with a fresh timer. User can start timer when ready.
- **Why find session by ID?** IDs are unique identifiers. More reliable than index-based lookup.
- **Why clearCurrentSession?** Useful when user navigates away. Keeps state clean.

### Testing Checklist:

After implementing session management, verify:
- [ ] `createNewSession("Test")` creates a new session
- [ ] New session appears in `allSessions` flow
- [ ] New session becomes `currentSession`
- [ ] `selectSession(id)` finds and selects correct session
- [ ] `selectSession(invalidId)` handles gracefully (sets currentSession to null)
- [ ] Timer resets when session changes
- [ ] `clearCurrentSession()` clears current session and resets timer

---

## Task 3.4: Implement Muscle Group CRUD Functions

**File Path:** `src/main/java/com/journal/ernie/viewmodel/WorkoutViewModel.kt`

**Purpose:** Implement functions to add and remove muscle groups from the current workout session.

### Step-by-Step Instructions:

1. **Implement `addMuscleGroup(name: String)` function:**
   - Function signature: `fun addMuscleGroup(name: String) { }`
   - Check if current session exists: `val session = _currentSession.value ?: return`
   - If no current session, return early (can't add to null session)
   - Create new muscle group: `val newGroup = MuscleGroup(name = name)`
   - Update current session using `.update { }`:
     ```kotlin
     _currentSession.update { currentSession ->
         currentSession?.copy(
             muscleGroups = currentSession.muscleGroups + newGroup
         )
     }
     ```
   - Wait, data classes with mutable lists don't work well with copy()
   - Better approach: Modify the session directly:
     ```kotlin
     session.addMuscleGroup(newGroup)
     _currentSession.value = session
     ```
   - Update allSessions to reflect the change:
     ```kotlin
     _allSessions.update { sessions ->
         sessions.map { if (it.id == session.id) session else it }
     }
     ```
   - Add TODO: `// TODO: Save to Room database when implemented`

2. **Implement `removeMuscleGroup(groupId: String)` function:**
   - Function signature: `fun removeMuscleGroup(groupId: String) { }`
   - Check if current session exists: `val session = _currentSession.value ?: return`
   - Remove muscle group from session: `session.removeMuscleGroup(groupId)`
   - Update current session: `_currentSession.value = session`
   - Update allSessions:
     ```kotlin
     _allSessions.update { sessions ->
         sessions.map { if (it.id == session.id) session else it }
     }
     ```
   - Add TODO: `// TODO: Delete from Room database when implemented`

3. **Add helper function: `findMuscleGroupById(groupId: String): MuscleGroup?` (optional):**
   - Function signature: `fun findMuscleGroupById(groupId: String): MuscleGroup?`
   - Returns muscle group from current session by ID
   - Implementation: `return _currentSession.value?.findMuscleGroupById(groupId)`
   - Useful for validation or finding groups before operations

### Expected Code Structure:

```kotlin
// Inside WorkoutViewModel class

fun addMuscleGroup(name: String) {
    viewModelScope.launch {
        val session = _currentSession.value ?: return@launch
        
        val newGroup = MuscleGroup(name = name)
        session.addMuscleGroup(newGroup)
        
        // Update current session
        _currentSession.value = session
        
        // Update all sessions list
        _allSessions.update { sessions ->
            sessions.map { if (it.id == session.id) session else it }
        }
        
        // TODO: Save to Room database when implemented
    }
}

fun removeMuscleGroup(groupId: String) {
    viewModelScope.launch {
        val session = _currentSession.value ?: return@launch
        
        session.removeMuscleGroup(groupId)
        
        // Update current session
        _currentSession.value = session
        
        // Update all sessions list
        _allSessions.update { sessions ->
            sessions.map { if (it.id == session.id) session else it }
        }
        
        // TODO: Delete from Room database when implemented
    }
}

fun findMuscleGroupById(groupId: String): MuscleGroup? {
    return _currentSession.value?.findMuscleGroupById(groupId)
}
```

### Important Notes:

- **Why check for null session?** Can't add muscle groups if no session is active. Early return prevents crashes.
- **Why update both currentSession and allSessions?** Current session is a reference to an object in allSessions. When we modify it, we need to update the list too so observers see the change.
- **Why use map to update allSessions?** Creates a new list with the updated session. StateFlow needs a new reference to trigger updates.
- **Why viewModelScope.launch?** Prepares for async database operations later. Even though it's synchronous now, using coroutines is good practice.

### Testing Checklist:

After implementing muscle group functions, verify:
- [ ] `addMuscleGroup("Chest")` adds group to current session
- [ ] Added group appears in currentSession.muscleGroups
- [ ] `removeMuscleGroup(id)` removes group from current session
- [ ] Removing non-existent group doesn't crash
- [ ] `addMuscleGroup()` with no current session does nothing (early return)
- [ ] `findMuscleGroupById(id)` returns correct group or null

---

## Task 3.5: Implement Exercise CRUD Functions

**File Path:** `src/main/java/com/journal/ernie/viewmodel/WorkoutViewModel.kt`

**Purpose:** Implement functions to add and remove exercises from muscle groups.

### Step-by-Step Instructions:

1. **Implement `addExercise(groupId: String, exerciseName: String)` function:**
   - Function signature: `fun addExercise(groupId: String, exerciseName: String) { }`
   - Check if current session exists: `val session = _currentSession.value ?: return`
   - Find muscle group: `val group = session.findMuscleGroupById(groupId) ?: return`
   - If group not found, return early
   - Create new exercise: `val newExercise = Exercise(name = exerciseName)`
   - Add exercise to group: `group.addExercise(newExercise)`
   - Update current session: `_currentSession.value = session`
   - Update allSessions:
     ```kotlin
     _allSessions.update { sessions ->
         sessions.map { if (it.id == session.id) session else it }
     }
     ```
   - Add TODO: `// TODO: Save to Room database when implemented`

2. **Implement `removeExercise(groupId: String, exerciseId: String)` function:**
   - Function signature: `fun removeExercise(groupId: String, exerciseId: String) { }`
   - Check if current session exists: `val session = _currentSession.value ?: return`
   - Find muscle group: `val group = session.findMuscleGroupById(groupId) ?: return`
   - Remove exercise from group: `group.removeExercise(exerciseId)`
   - Update current session: `_currentSession.value = session`
   - Update allSessions:
     ```kotlin
     _allSessions.update { sessions ->
         sessions.map { if (it.id == session.id) session else it }
     }
     ```
   - Add TODO: `// TODO: Delete from Room database when implemented`

3. **Add helper function: `findExerciseById(groupId: String, exerciseId: String): Exercise?` (optional):**
   - Function signature: `fun findExerciseById(groupId: String, exerciseId: String): Exercise?`
   - Finds exercise within a muscle group
   - Implementation:
     ```kotlin
     val group = findMuscleGroupById(groupId) ?: return null
     return group.findExerciseById(exerciseId)
     ```
   - Useful for validation or finding exercises before operations

### Expected Code Structure:

```kotlin
// Inside WorkoutViewModel class

fun addExercise(groupId: String, exerciseName: String) {
    viewModelScope.launch {
        val session = _currentSession.value ?: return@launch
        val group = session.findMuscleGroupById(groupId) ?: return@launch
        
        val newExercise = Exercise(name = exerciseName)
        group.addExercise(newExercise)
        
        // Update current session
        _currentSession.value = session
        
        // Update all sessions list
        _allSessions.update { sessions ->
            sessions.map { if (it.id == session.id) session else it }
        }
        
        // TODO: Save to Room database when implemented
    }
}

fun removeExercise(groupId: String, exerciseId: String) {
    viewModelScope.launch {
        val session = _currentSession.value ?: return@launch
        val group = session.findMuscleGroupById(groupId) ?: return@launch
        
        group.removeExercise(exerciseId)
        
        // Update current session
        _currentSession.value = session
        
        // Update all sessions list
        _allSessions.update { sessions ->
            sessions.map { if (it.id == session.id) session else it }
        }
        
        // TODO: Delete from Room database when implemented
    }
}

fun findExerciseById(groupId: String, exerciseId: String): Exercise? {
    val group = findMuscleGroupById(groupId) ?: return null
    return group.findExerciseById(exerciseId)
}
```

### Important Notes:

- **Why check both session and group?** Need to verify both exist before adding exercise. Early returns prevent crashes.
- **Why findMuscleGroupById first?** Need to get the group reference before we can add exercises to it.
- **Why same update pattern?** Same pattern as muscle groups - update current session, then update allSessions list.
- **Why two IDs?** Need groupId to find the group, then exerciseId to find/remove the exercise within that group.

### Testing Checklist:

After implementing exercise functions, verify:
- [ ] `addExercise(groupId, "Bench Press")` adds exercise to correct group
- [ ] Added exercise appears in group.exercises
- [ ] `removeExercise(groupId, exerciseId)` removes exercise from group
- [ ] Removing non-existent exercise doesn't crash
- [ ] `addExercise()` with invalid groupId does nothing (early return)
- [ ] `findExerciseById()` returns correct exercise or null

---

## Task 3.6: Implement Set CRUD Functions

**File Path:** `src/main/java/com/journal/ernie/viewmodel/WorkoutViewModel.kt`

**Purpose:** Implement functions to add, remove, and update sets within exercises.

### Step-by-Step Instructions:

1. **Implement `addSet(groupId: String, exerciseId: String)` function:**
   - Function signature: `fun addSet(groupId: String, exerciseId: String) { }`
   - Check if current session exists: `val session = _currentSession.value ?: return`
   - Find muscle group: `val group = session.findMuscleGroupById(groupId) ?: return`
   - Find exercise: `val exercise = group.findExerciseById(exerciseId) ?: return`
   - Create new set with defaults: `val newSet = SetEntry()`
   - Add set to exercise: `exercise.addSet(newSet)`
   - Update current session: `_currentSession.value = session`
   - Update allSessions:
     ```kotlin
     _allSessions.update { sessions ->
         sessions.map { if (it.id == session.id) session else it }
     }
     ```
   - Add TODO: `// TODO: Save to Room database when implemented`

2. **Implement `removeSet(groupId: String, exerciseId: String, setIndex: Int)` function:**
   - Function signature: `fun removeSet(groupId: String, exerciseId: String, setIndex: Int) { }`
   - Check if current session exists: `val session = _currentSession.value ?: return`
   - Find muscle group: `val group = session.findMuscleGroupById(groupId) ?: return`
   - Find exercise: `val exercise = group.findExerciseById(exerciseId) ?: return`
   - Remove set by index: `exercise.removeSet(setIndex)`
   - Update current session: `_currentSession.value = session`
   - Update allSessions:
     ```kotlin
     _allSessions.update { sessions ->
         sessions.map { if (it.id == session.id) session else it }
     }
     ```
   - Add TODO: `// TODO: Delete from Room database when implemented`

3. **Implement `updateSet(groupId: String, exerciseId: String, setIndex: Int, reps: Int, weight: Float, comment: String?)` function:**
   - Function signature: `fun updateSet(groupId: String, exerciseId: String, setIndex: Int, reps: Int, weight: Float, comment: String?) { }`
   - Check if current session exists: `val session = _currentSession.value ?: return`
   - Find muscle group: `val group = session.findMuscleGroupById(groupId) ?: return`
   - Find exercise: `val exercise = group.findExerciseById(exerciseId) ?: return`
   - Check if setIndex is valid: `if (setIndex !in exercise.sets.indices) return`
   - Get the set: `val set = exercise.sets[setIndex]`
   - Update set properties:
     ```kotlin
     set.reps = reps
     set.weight = weight
     set.comment = comment
     ```
   - Update current session: `_currentSession.value = session`
   - Update allSessions:
     ```kotlin
     _allSessions.update { sessions ->
         sessions.map { if (it.id == session.id) session else it }
     }
     ```
   - Add TODO: `// TODO: Update in Room database when implemented`

4. **Add alternative `removeSetById()` function (optional but recommended):**
   - Function signature: `fun removeSetById(groupId: String, exerciseId: String, setId: String) { }`
   - Similar to removeSet but uses ID instead of index
   - More reliable than index-based removal
   - Implementation:
     ```kotlin
     val exercise = findExerciseById(groupId, exerciseId) ?: return
     exercise.removeSetById(setId)
     // Then update sessions as above
     ```

### Expected Code Structure:

```kotlin
// Inside WorkoutViewModel class

fun addSet(groupId: String, exerciseId: String) {
    viewModelScope.launch {
        val session = _currentSession.value ?: return@launch
        val group = session.findMuscleGroupById(groupId) ?: return@launch
        val exercise = group.findExerciseById(exerciseId) ?: return@launch
        
        val newSet = SetEntry()
        exercise.addSet(newSet)
        
        // Update current session
        _currentSession.value = session
        
        // Update all sessions list
        _allSessions.update { sessions ->
            sessions.map { if (it.id == session.id) session else it }
        }
        
        // TODO: Save to Room database when implemented
    }
}

fun removeSet(groupId: String, exerciseId: String, setIndex: Int) {
    viewModelScope.launch {
        val session = _currentSession.value ?: return@launch
        val group = session.findMuscleGroupById(groupId) ?: return@launch
        val exercise = group.findExerciseById(exerciseId) ?: return@launch
        
        exercise.removeSet(setIndex)
        
        // Update current session
        _currentSession.value = session
        
        // Update all sessions list
        _allSessions.update { sessions ->
            sessions.map { if (it.id == session.id) session else it }
        }
        
        // TODO: Delete from Room database when implemented
    }
}

fun updateSet(
    groupId: String,
    exerciseId: String,
    setIndex: Int,
    reps: Int,
    weight: Float,
    comment: String?
) {
    viewModelScope.launch {
        val session = _currentSession.value ?: return@launch
        val group = session.findMuscleGroupById(groupId) ?: return@launch
        val exercise = group.findExerciseById(exerciseId) ?: return@launch
        
        // Validate index
        if (setIndex !in exercise.sets.indices) return@launch
        
        // Update set
        val set = exercise.sets[setIndex]
        set.reps = reps
        set.weight = weight
        set.comment = comment
        
        // Update current session
        _currentSession.value = session
        
        // Update all sessions list
        _allSessions.update { sessions ->
            sessions.map { if (it.id == session.id) session else it }
        }
        
        // TODO: Update in Room database when implemented
    }
}

fun removeSetById(groupId: String, exerciseId: String, setId: String) {
    viewModelScope.launch {
        val session = _currentSession.value ?: return@launch
        val group = session.findMuscleGroupById(groupId) ?: return@launch
        val exercise = group.findExerciseById(exerciseId) ?: return@launch
        
        exercise.removeSetById(setId)
        
        // Update current session
        _currentSession.value = session
        
        // Update all sessions list
        _allSessions.update { sessions ->
            sessions.map { if (it.id == session.id) session else it }
        }
        
        // TODO: Delete from Room database when implemented
    }
}
```

### Important Notes:

- **Why check setIndex validity?** Prevents IndexOutOfBoundsException if invalid index is passed. Early return is safer than crashing.
- **Why direct property assignment?** SetEntry properties are `var`, so we can modify them directly. This is simpler than creating a new SetEntry.
- **Why nullable comment?** Comment is optional. Passing `null` clears the comment.
- **Why both removeSet and removeSetById?** Index-based is simpler but fragile. ID-based is more reliable. Having both gives flexibility.

### Testing Checklist:

After implementing set functions, verify:
- [ ] `addSet(groupId, exerciseId)` adds set to correct exercise
- [ ] Added set has default values (reps=0, weight=0.0f, comment=null)
- [ ] `removeSet()` removes set at correct index
- [ ] `removeSetById()` removes set by ID
- [ ] `updateSet()` updates set properties correctly
- [ ] Invalid setIndex doesn't crash (early return)
- [ ] Updates are reflected in currentSession and allSessions

---

## Task 3.7: Implement Timer Functions

**File Path:** `src/main/java/com/journal/ernie/viewmodel/WorkoutViewModel.kt`

**Purpose:** Implement functions to start, pause, and reset the workout timer.

### Step-by-Step Instructions:

1. **Add private timer job variable:**
   - Add property: `private var timerJob: kotlinx.coroutines.Job? = null`
   - This will hold the coroutine job that updates the timer
   - Allows us to cancel the timer when needed

2. **Implement `startTimer()` function:**
   - Function signature: `fun startTimer() { }`
   - Check if timer is already running: `if (_timerState.value.isRunning) return`
   - Cancel any existing timer job: `timerJob?.cancel()`
   - Start new timer job in `viewModelScope.launch { }`:
     ```kotlin
     timerJob = viewModelScope.launch {
         while (true) {
             delay(1000) // Wait 1 second
             _timerState.update { currentState ->
                 currentState.copy(
                     elapsedTimeSeconds = currentState.elapsedTimeSeconds + 1,
                     isRunning = true
                 )
             }
         }
     }
     ```
   - This increments elapsedTimeSeconds every second while running

3. **Implement `pauseTimer()` function:**
   - Function signature: `fun pauseTimer() { }`
   - Cancel timer job: `timerJob?.cancel()`
   - Set `timerJob = null`
   - Update timer state to not running:
     ```kotlin
     _timerState.update { currentState ->
         currentState.copy(isRunning = false)
     }
     ```

4. **Implement `resetTimer()` function:**
   - Function signature: `fun resetTimer() { }`
   - Cancel timer job: `timerJob?.cancel()`
   - Set `timerJob = null`
   - Reset timer state to initial:
     ```kotlin
     _timerState.value = TimerState.initial()
     ```

5. **Add cleanup in ViewModel (override onCleared):**
   - Override function: `override fun onCleared() { }`
   - Cancel timer job: `timerJob?.cancel()`
   - This ensures timer stops when ViewModel is destroyed

6. **Add import for delay:**
   - `import kotlinx.coroutines.delay`

### Expected Code Structure:

```kotlin
// Inside WorkoutViewModel class

// Add at top of class with other properties
private var timerJob: kotlinx.coroutines.Job? = null

// Timer functions
fun startTimer() {
    // Don't start if already running
    if (_timerState.value.isRunning) return
    
    // Cancel any existing job
    timerJob?.cancel()
    
    // Start new timer
    timerJob = viewModelScope.launch {
        while (true) {
            delay(1000) // Wait 1 second
            _timerState.update { currentState ->
                currentState.copy(
                    elapsedTimeSeconds = currentState.elapsedTimeSeconds + 1,
                    isRunning = true
                )
            }
        }
    }
}

fun pauseTimer() {
    timerJob?.cancel()
    timerJob = null
    
    _timerState.update { currentState ->
        currentState.copy(isRunning = false)
    }
}

fun resetTimer() {
    timerJob?.cancel()
    timerJob = null
    
    _timerState.value = TimerState.initial()
}

override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
}
```

### Important Notes:

- **Why timerJob variable?** Need to hold reference to the coroutine so we can cancel it when pausing or resetting.
- **Why check isRunning before start?** Prevents multiple timer jobs running simultaneously. Only one timer should run at a time.
- **Why while(true) loop?** Timer needs to run continuously, incrementing every second. Loop continues until cancelled.
- **Why delay(1000)?** Waits 1000 milliseconds (1 second) before incrementing. This creates the timer tick.
- **Why copy() in update?** Creates new TimerState instance. StateFlow needs new object reference to trigger updates.
- **Why onCleared()?** Ensures timer stops when ViewModel is destroyed. Prevents memory leaks and unnecessary work.

### Testing Checklist:

After implementing timer functions, verify:
- [ ] `startTimer()` starts the timer (isRunning becomes true)
- [ ] Timer increments elapsedTimeSeconds every second
- [ ] `pauseTimer()` stops the timer (isRunning becomes false)
- [ ] Timer doesn't increment when paused
- [ ] `resetTimer()` resets elapsedTimeSeconds to 0 and stops timer
- [ ] Starting timer when already running doesn't create duplicate timers
- [ ] Timer stops when ViewModel is cleared

---

## Phase 3 Complete: Final Verification

After completing all tasks, run these comprehensive tests:

### Compilation Test:
- [ ] Project builds successfully (Build → Make Project)
- [ ] No red error underlines in any file
- [ ] All imports resolve correctly

### State Management Tests:
- [ ] ViewModel can be instantiated
- [ ] All StateFlow properties are accessible
- [ ] Initial state is correct (null session, empty sessions list, initial timer state)

### Session Management Tests:
- [ ] `createNewSession()` creates session and adds to allSessions
- [ ] New session becomes currentSession
- [ ] `selectSession(id)` selects correct session
- [ ] `selectSession(invalidId)` handles gracefully
- [ ] `clearCurrentSession()` clears current session

### Muscle Group Tests:
- [ ] `addMuscleGroup()` adds group to current session
- [ ] `removeMuscleGroup()` removes group from current session
- [ ] Operations fail gracefully if no current session

### Exercise Tests:
- [ ] `addExercise()` adds exercise to correct muscle group
- [ ] `removeExercise()` removes exercise from group
- [ ] Operations fail gracefully if group/exercise not found

### Set Tests:
- [ ] `addSet()` adds set to exercise with default values
- [ ] `removeSet()` removes set by index
- [ ] `removeSetById()` removes set by ID
- [ ] `updateSet()` updates set properties
- [ ] Invalid indices don't crash

### Timer Tests:
- [ ] `startTimer()` starts timer and increments seconds
- [ ] `pauseTimer()` pauses timer
- [ ] `resetTimer()` resets timer to 0
- [ ] Timer doesn't run multiple instances
- [ ] Timer stops on ViewModel destruction

### Integration Tests:
- [ ] Create session → Add muscle group → Add exercise → Add set → Update set
- [ ] Full workflow works end-to-end
- [ ] State updates are reflected in StateFlow
- [ ] Multiple operations don't interfere with each other

### Code Quality:
- [ ] All functions use viewModelScope.launch where appropriate
- [ ] All TODO comments are present for Room integration
- [ ] No unused imports
- [ ] Code follows Kotlin conventions
- [ ] Error handling is present (null checks, early returns)

---

## Common Issues & Solutions

### Issue: "Unresolved reference: ViewModel"
**Solution:** Add import: `import androidx.lifecycle.ViewModel`
**Also check:** Ensure lifecycle-viewmodel dependency is in build.gradle (but don't modify Gradle per instructions)

### Issue: "Unresolved reference: StateFlow"
**Solution:** Add import: `import kotlinx.coroutines.flow.StateFlow`
**Also check:** Ensure kotlinx-coroutines-core dependency is in build.gradle

### Issue: "Unresolved reference: viewModelScope"
**Solution:** Add import: `import androidx.lifecycle.viewModelScope`
**Also check:** Ensure lifecycle-viewmodel-ktx dependency is in build.gradle

### Issue: StateFlow doesn't update UI
**Solution:** 
- Ensure you're using `.asStateFlow()` for public properties
- Check that UI is collecting the StateFlow correctly (will be done in Phase 4)
- Verify `.update { }` is creating a new object reference

### Issue: Timer doesn't increment
**Solution:**
- Check that `delay(1000)` import is present
- Verify `while(true)` loop is inside `viewModelScope.launch`
- Check that timerJob is not being cancelled prematurely
- Ensure `isRunning` check allows timer to start

### Issue: "Cannot find symbol: copy"
**Solution:** 
- TimerState must be a `data class` to have `copy()` method
- Verify TimerState.kt uses `data class` keyword

### Issue: Modifications not reflected in StateFlow
**Solution:**
- Ensure you're updating StateFlow with `.value =` or `.update { }`
- For mutable lists, you may need to create a new list reference
- Check that you're updating both `_currentSession` and `_allSessions`

### Issue: Multiple timer instances running
**Solution:**
- Check `isRunning` before starting timer
- Cancel existing `timerJob` before creating new one
- Verify `pauseTimer()` cancels the job

---

## Architecture Notes

### State Management Pattern:

```
UI Layer (Phase 4+)
    ↓ observes
StateFlow (read-only)
    ↓ exposed by
ViewModel
    ↓ updates
MutableStateFlow (private)
    ↓ holds
Data Models (Phase 1)
```

### Data Flow Example:

1. **User Action:** Taps "Add Muscle Group" button
2. **UI Calls:** `viewModel.addMuscleGroup("Chest")`
3. **ViewModel:** Updates `_currentSession` MutableStateFlow
4. **StateFlow:** Emits new value to collectors
5. **UI:** Recomposes with new data

### Why This Pattern:

- **Separation of Concerns:** UI doesn't directly modify data
- **Single Source of Truth:** ViewModel is the only place that modifies state
- **Reactive Updates:** StateFlow automatically notifies observers
- **Testability:** ViewModel can be tested independently of UI

---

## Next Steps: Phase 4

After Phase 3 is complete and verified, you're ready for Phase 4:
- Create WorkoutListScreen to display all sessions
- Wire up ViewModel to UI
- Implement FAB to create new sessions
- Display session list with cards
- This will connect the ViewModel to the UI layer

**Phase 3 establishes the business logic layer. Phase 4 will connect it to the UI.**

---

## Summary

Phase 3 creates:
- ✅ TimerState data class for timer state management
- ✅ WorkoutViewModel with StateFlow state management
- ✅ Session management functions (create, load, select)
- ✅ Muscle group CRUD operations
- ✅ Exercise CRUD operations
- ✅ Set CRUD operations
- ✅ Timer functions (start, pause, reset)

**Total Files Created:** 2 new files (TimerState.kt, WorkoutViewModel.kt)
**Total Files Modified:** 0 files (but will be used by Phase 4+)
**Total Lines of Code:** ~400-500 lines
**Time Estimate:** 3-4 hours for careful implementation

The ViewModel is now complete and ready to be connected to the UI in Phase 4. All business logic is implemented and ready for Room database integration in later phases.

# Phase 1: Data Models & Basic Structure - Detailed Implementation Guide

## Overview

This document provides step-by-step instructions for implementing Phase 1 of the Workout Tracking App. Phase 1 establishes the foundation: data models, navigation structure, and basic Compose setup.

**Goal:** Create all data models, set up basic navigation, and prepare the app structure for future phases.

**Prerequisites:** 
- Android Studio project with Jetpack Compose configured
- Basic Kotlin knowledge
- Understanding of data classes and Compose basics

---

## Directory Structure Setup

Before starting, verify or create these directories:

```
src/main/java/com/journal/ernie/
├── data/          (create this folder)
├── ui/            (create this folder)
└── viewmodel/     (create this folder - will be used in Phase 3)
```

**How to create:**
- In Android Studio, right-click on `com/journal/ernie/` package
- Select "New" → "Package"
- Enter `data` and click OK
- Repeat for `ui` and `viewmodel`

**Note:** Directories are usually created automatically when you create the first file in them, but it's good to verify they exist.

---

## Task 1.1: Create SetEntry Data Model

**File Path:** `src/main/java/com/journal/ernie/data/SetEntry.kt`

**Purpose:** Represents a single set within an exercise. A set contains the number of reps, weight used, and an optional comment.

### Step-by-Step Instructions:

1. **Create the file:**
   - Right-click on `data` package folder
   - Select "New" → "Kotlin Class/File"
   - Name: `SetEntry`
   - Type: "Data Class"

2. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.data
   ```

3. **Create the data class structure:**
   - Use `data class` keyword (not regular `class`)
   - This provides automatic `equals()`, `hashCode()`, `toString()`, and `copy()` methods

4. **Add properties with exact specifications:**
   - `id: String` - Unique identifier for this set
     - Use `java.util.UUID.randomUUID().toString()` to generate unique IDs
     - This will be used to identify sets when removing or updating them
   - `reps: Int` - Number of repetitions performed
     - Default value: `0`
     - Represents how many times the exercise was performed
   - `weight: Float` - Weight used (in kg or lbs, depending on user preference)
     - Default value: `0.0f`
     - Use `Float` type (not `Double`) for memory efficiency
   - `comment: String?` - Optional comment about this set
     - Type: `String?` (nullable)
     - Default value: `null`
     - Users can add notes like "felt easy" or "last rep was hard"

5. **Add constructor:**
   - Data classes automatically generate constructors
   - Make sure all properties have default values OR are in the primary constructor
   - Recommended: Put all properties in primary constructor with default values

6. **Add TODO comment:**
   - Add comment: `// TODO: Add @Entity annotation when Room is integrated`
   - This reminds us that this will become a Room entity later

### Expected Code Structure:

```kotlin
package com.journal.ernie.data

// TODO: Add @Entity annotation when Room is integrated
data class SetEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    var reps: Int = 0,
    var weight: Float = 0.0f,
    var comment: String? = null
)
```

### Important Notes:

- **Why `var` for reps, weight, comment?** These need to be mutable so users can edit them later. `id` is `val` because it never changes.
- **Why UUID for ID?** UUIDs are globally unique, preventing ID collisions even if multiple sets are created simultaneously.
- **Why nullable comment?** Not all sets need comments, so making it optional reduces unnecessary empty strings.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] You can create a SetEntry: `SetEntry()`
- [ ] You can create with values: `SetEntry(reps = 10, weight = 50.0f, comment = "Easy")`
- [ ] Default values work: `SetEntry()` should have reps=0, weight=0.0f, comment=null

---

## Task 1.2: Create Exercise Data Model

**File Path:** `src/main/java/com/journal/ernie/data/Exercise.kt`

**Purpose:** Represents an exercise within a muscle group. An exercise has a name and contains multiple sets.

### Step-by-Step Instructions:

1. **Create the file:**
   - Right-click on `data` package folder
   - Select "New" → "Kotlin Class/File"
   - Name: `Exercise`
   - Type: "Data Class"

2. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.data
   ```
   - Same package as SetEntry since they're related

3. **Import SetEntry:**
   - Since Exercise contains SetEntry objects, you don't need an import (same package)
   - But if you reference it, Kotlin will find it automatically

4. **Create the data class with properties:**
   - `id: String` - Unique identifier for this exercise
     - Default: Generate using `java.util.UUID.randomUUID().toString()`
   - `name: String` - Name of the exercise (e.g., "Bench Press", "Squats")
     - No default value - must be provided when creating
     - This is the exercise name the user enters
   - `sets: MutableList<SetEntry>` - List of sets performed for this exercise
     - Type: `MutableList` (not `List`) so we can add/remove sets
     - Default: Empty list `mutableListOf<SetEntry>()`
     - Contains all the sets the user performs for this exercise

5. **Add method: `addSet(set: SetEntry)`**
   - Purpose: Add a new set to this exercise
   - Implementation: `sets.add(set)`
   - No return value (Unit)
   - This will be called when user taps "Add Set" button

6. **Add method: `removeSet(index: Int)`**
   - Purpose: Remove a set at a specific index
   - Implementation: Check if index is valid, then remove
   - Add bounds checking: `if (index in sets.indices) { sets.removeAt(index) }`
   - This prevents crashes if invalid index is passed
   - No return value (Unit)

7. **Add method: `removeSetById(id: String)`**
   - Purpose: Remove a set by its ID (more reliable than index)
   - Implementation: `sets.removeAll { it.id == id }`
   - This is safer than using index because list order might change
   - Returns: `Boolean` (true if removed, false if not found)

8. **Add helper method (optional but recommended): `getSetCount(): Int`**
   - Purpose: Get the number of sets
   - Implementation: `return sets.size`
   - Makes code more readable than `exercise.sets.size`

9. **Add TODO comment:**
   - `// TODO: Add @Entity annotation when Room is integrated`

### Expected Code Structure:

```kotlin
package com.journal.ernie.data

// TODO: Add @Entity annotation when Room is integrated
data class Exercise(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val sets: MutableList<SetEntry> = mutableListOf()
) {
    fun addSet(set: SetEntry) {
        sets.add(set)
    }
    
    fun removeSet(index: Int) {
        if (index in sets.indices) {
            sets.removeAt(index)
        }
    }
    
    fun removeSetById(id: String): Boolean {
        return sets.removeAll { it.id == id }
    }
    
    fun getSetCount(): Int = sets.size
}
```

### Important Notes:

- **Why MutableList?** We need to modify the list (add/remove sets), so it must be mutable.
- **Why both removeSet methods?** Index-based removal is fast but fragile. ID-based removal is safer but slightly slower. Having both gives flexibility.
- **Why `val` for id and name?** These shouldn't change after creation. If user wants to rename, create a new Exercise.
- **Why `val` for sets list but mutable?** The list reference doesn't change, but the contents do. This is a common pattern.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] You can create an Exercise: `Exercise(name = "Bench Press")`
- [ ] You can add a set: `exercise.addSet(SetEntry(reps = 10, weight = 50.0f))`
- [ ] You can remove a set by index: `exercise.removeSet(0)`
- [ ] You can remove a set by ID: `exercise.removeSetById(someId)`
- [ ] Bounds checking works: `exercise.removeSet(999)` doesn't crash

---

## Task 1.3: Create MuscleGroup Data Model

**File Path:** `src/main/java/com/journal/ernie/data/MuscleGroup.kt`

**Purpose:** Represents a muscle group within a workout session. A muscle group has a name and contains multiple exercises.

### Step-by-Step Instructions:

1. **Create the file:**
   - Right-click on `data` package folder
   - Select "New" → "Kotlin Class/File"
   - Name: `MuscleGroup`
   - Type: "Data Class"

2. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.data
   ```

3. **Create the data class with properties:**
   - `id: String` - Unique identifier for this muscle group
     - Default: Generate using `java.util.UUID.randomUUID().toString()`
   - `name: String` - Name of the muscle group (e.g., "Chest", "Back", "Legs")
     - No default value - must be provided
     - Can be a preset name or custom name
   - `exercises: MutableList<Exercise>` - List of exercises in this muscle group
     - Type: `MutableList<Exercise>`
     - Default: Empty list `mutableListOf<Exercise>()`
     - Contains all exercises targeting this muscle group

4. **Add method: `addExercise(exercise: Exercise)`**
   - Purpose: Add a new exercise to this muscle group
   - Implementation: `exercises.add(exercise)`
   - No return value (Unit)
   - Called when user adds an exercise to a muscle group

5. **Add method: `removeExercise(id: String)`**
   - Purpose: Remove an exercise by its ID
   - Implementation: `exercises.removeAll { it.id == id }`
   - Returns: `Boolean` (true if removed, false if not found)
   - ID-based removal is safer than index-based

6. **Add method: `findExerciseById(id: String): Exercise?`**
   - Purpose: Find an exercise by its ID
   - Implementation: `return exercises.find { it.id == id }`
   - Returns: `Exercise?` (nullable - returns null if not found)
   - This is useful when you need to update a specific exercise

7. **Add helper method (optional but recommended): `getExerciseCount(): Int`**
   - Purpose: Get the number of exercises
   - Implementation: `return exercises.size`
   - Makes code more readable

8. **Add TODO comment:**
   - `// TODO: Add @Entity annotation when Room is integrated`

### Expected Code Structure:

```kotlin
package com.journal.ernie.data

// TODO: Add @Entity annotation when Room is integrated
data class MuscleGroup(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val exercises: MutableList<Exercise> = mutableListOf()
) {
    fun addExercise(exercise: Exercise) {
        exercises.add(exercise)
    }
    
    fun removeExercise(id: String): Boolean {
        return exercises.removeAll { it.id == id }
    }
    
    fun findExerciseById(id: String): Exercise? {
        return exercises.find { it.id == id }
    }
    
    fun getExerciseCount(): Int = exercises.size
}
```

### Important Notes:

- **Same pattern as Exercise:** MuscleGroup follows the same structure as Exercise, but contains Exercise objects instead of SetEntry objects.
- **Why findExerciseById?** When updating sets within an exercise, we need to find the exercise first. This method makes that easy.
- **Why only ID-based removal?** Index-based removal is less reliable for exercises since the list can change. ID-based is always safe.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] You can create a MuscleGroup: `MuscleGroup(name = "Chest")`
- [ ] You can add an exercise: `group.addExercise(Exercise(name = "Bench Press"))`
- [ ] You can remove an exercise: `group.removeExercise(exerciseId)`
- [ ] You can find an exercise: `group.findExerciseById(exerciseId)` returns the exercise or null
- [ ] Helper method works: `group.getExerciseCount()` returns correct count

---

## Task 1.4: Create WorkoutSession Data Model

**File Path:** `src/main/java/com/journal/ernie/data/WorkoutSession.kt`

**Purpose:** Represents a complete workout session. A session has a name, date, and contains multiple muscle groups.

### Step-by-Step Instructions:

1. **Create the file:**
   - Right-click on `data` package folder
   - Select "New" → "Kotlin Class/File"
   - Name: `WorkoutSession`
   - Type: "Data Class"

2. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.data
   ```

3. **Create the data class with properties:**
   - `id: String` - Unique identifier for this workout session
     - Default: Generate using `java.util.UUID.randomUUID().toString()`
   - `name: String` - Name/title of the workout session
     - No default value - must be provided
     - Examples: "Morning Workout", "Leg Day", "Full Body"
   - `date: Long` - Timestamp when the session was created
     - Type: `Long` (milliseconds since epoch)
     - Default: `System.currentTimeMillis()`
     - Used to sort sessions by date
   - `muscleGroups: MutableList<MuscleGroup>` - List of muscle groups in this session
     - Type: `MutableList<MuscleGroup>`
     - Default: Empty list `mutableListOf<MuscleGroup>()`
     - Contains all muscle groups worked in this session

4. **Add method: `addMuscleGroup(group: MuscleGroup)`**
   - Purpose: Add a new muscle group to this session
   - Implementation: `muscleGroups.add(group)`
   - No return value (Unit)

5. **Add method: `removeMuscleGroup(id: String)`**
   - Purpose: Remove a muscle group by its ID
   - Implementation: `muscleGroups.removeAll { it.id == id }`
   - Returns: `Boolean` (true if removed, false if not found)

6. **Add method: `findMuscleGroupById(id: String): MuscleGroup?`**
   - Purpose: Find a muscle group by its ID
   - Implementation: `return muscleGroups.find { it.id == id }`
   - Returns: `MuscleGroup?` (nullable)
   - Used when adding exercises to a specific muscle group

7. **Add companion object with factory function:**
   - Create `companion object { }` block inside the data class
   - Add function: `fun createNew(name: String): WorkoutSession`
   - Implementation:
     ```kotlin
     return WorkoutSession(
         id = java.util.UUID.randomUUID().toString(),
         name = name,
         date = System.currentTimeMillis(),
         muscleGroups = mutableListOf()
     )
     ```
   - This factory function simplifies creating new sessions with proper defaults

8. **Add helper method (optional but recommended): `getTotalExerciseCount(): Int`**
   - Purpose: Get total number of exercises across all muscle groups
   - Implementation: `return muscleGroups.sumOf { it.exercises.size }`
   - Useful for displaying session summary

9. **Add TODO comment:**
   - `// TODO: Add @Entity annotation when Room is integrated`

### Expected Code Structure:

```kotlin
package com.journal.ernie.data

// TODO: Add @Entity annotation when Room is integrated
data class WorkoutSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val date: Long = System.currentTimeMillis(),
    val muscleGroups: MutableList<MuscleGroup> = mutableListOf()
) {
    fun addMuscleGroup(group: MuscleGroup) {
        muscleGroups.add(group)
    }
    
    fun removeMuscleGroup(id: String): Boolean {
        return muscleGroups.removeAll { it.id == id }
    }
    
    fun findMuscleGroupById(id: String): MuscleGroup? {
        return muscleGroups.find { it.id == id }
    }
    
    fun getTotalExerciseCount(): Int {
        return muscleGroups.sumOf { it.exercises.size }
    }
    
    companion object {
        fun createNew(name: String): WorkoutSession {
            return WorkoutSession(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                date = System.currentTimeMillis(),
                muscleGroups = mutableListOf()
            )
        }
    }
}
```

### Important Notes:

- **Why factory function?** `createNew()` ensures consistent session creation with proper ID generation and timestamp. It's cleaner than calling the constructor directly.
- **Why System.currentTimeMillis()?** This gives the current time in milliseconds since January 1, 1970 (Unix epoch). It's a standard way to represent dates as numbers.
- **Why getTotalExerciseCount()?** Useful for displaying summaries like "This session has 8 exercises" without manually counting.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] You can create a session: `WorkoutSession(name = "Morning Workout")`
- [ ] Factory function works: `WorkoutSession.createNew("Leg Day")` creates a session with current timestamp
- [ ] You can add a muscle group: `session.addMuscleGroup(MuscleGroup(name = "Chest"))`
- [ ] You can remove a muscle group: `session.removeMuscleGroup(groupId)`
- [ ] You can find a muscle group: `session.findMuscleGroupById(groupId)` returns the group or null
- [ ] Helper method works: `session.getTotalExerciseCount()` returns correct total

---

## Task 1.5: Create MuscleGroupPresets

**File Path:** `src/main/java/com/journal/ernie/data/MuscleGroupPresets.kt`

**Purpose:** Provides a list of common muscle group names that users can select from, plus helper functions for validation and suggestions.

### Step-by-Step Instructions:

1. **Create the file:**
   - Right-click on `data` package folder
   - Select "New" → "Kotlin Class/File"
   - Name: `MuscleGroupPresets`
   - Type: "Object" (not class or data class)

2. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.data
   ```

3. **Create object (singleton):**
   - Use `object` keyword (not `class`)
   - Objects in Kotlin are singletons - only one instance exists
   - No need to instantiate - access directly: `MuscleGroupPresets.getAllPresets()`

4. **Add private property for preset list:**
   - Name: `PRESET_GROUPS`
   - Type: `private val PRESET_GROUPS: List<String>`
   - Value: List of common muscle group names:
     ```kotlin
     private val PRESET_GROUPS = listOf(
         "Chest",
         "Back",
         "Legs",
         "Shoulders",
         "Arms",
         "Core",
         "Cardio",
         "Full Body"
     )
     ```
   - Make it `private` so it can't be modified from outside
   - Use `val` (immutable) to prevent accidental changes

5. **Add public function: `getAllPresets(): List<String>`**
   - Purpose: Get a copy of all preset muscle group names
   - Implementation: `return PRESET_GROUPS.toList()`
   - Returns a copy (not the original list) to prevent external modification
   - This will be used to populate a dropdown or list in the UI

6. **Add public function: `isPreset(name: String): Boolean`**
   - Purpose: Check if a given name is one of the presets (case-insensitive)
   - Implementation:
     ```kotlin
     return PRESET_GROUPS.any { 
         it.equals(name, ignoreCase = true) 
     }
     ```
   - Uses `equals(ignoreCase = true)` for case-insensitive comparison
   - Returns `true` if name matches any preset, `false` otherwise
   - Useful for validation or highlighting preset names in UI

7. **Add public function: `getPresetSuggestions(query: String): List<String>`**
   - Purpose: Get preset names that contain the query string (for autocomplete)
   - Implementation:
     ```kotlin
     return PRESET_GROUPS.filter { 
         it.contains(query, ignoreCase = true) 
     }
     ```
   - Filters presets that contain the query (case-insensitive)
   - Returns filtered list
   - If query is empty, returns all presets
   - Used for autocomplete/suggestions when user types

8. **Optional: Add function to get random preset (for testing):**
   - Not required, but can be useful: `fun getRandomPreset(): String = PRESET_GROUPS.random()`

### Expected Code Structure:

```kotlin
package com.journal.ernie.data

object MuscleGroupPresets {
    private val PRESET_GROUPS = listOf(
        "Chest",
        "Back",
        "Legs",
        "Shoulders",
        "Arms",
        "Core",
        "Cardio",
        "Full Body"
    )
    
    fun getAllPresets(): List<String> {
        return PRESET_GROUPS.toList()
    }
    
    fun isPreset(name: String): Boolean {
        return PRESET_GROUPS.any { 
            it.equals(name, ignoreCase = true) 
        }
    }
    
    fun getPresetSuggestions(query: String): List<String> {
        return PRESET_GROUPS.filter { 
            it.contains(query, ignoreCase = true) 
        }
    }
}
```

### Important Notes:

- **Why object instead of class?** Objects are singletons - perfect for utility functions that don't need state. No need to create instances.
- **Why private PRESET_GROUPS?** Prevents external code from modifying the list. We control access through functions.
- **Why return a copy in getAllPresets()?** Prevents external code from modifying the original list. Kotlin's `toList()` creates a new list.
- **Why case-insensitive matching?** Better user experience - "chest", "Chest", and "CHEST" all match.
- **Why filter for suggestions?** As user types, we show matching presets. Empty query shows all presets.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] You can get all presets: `MuscleGroupPresets.getAllPresets()` returns list of 8 items
- [ ] Case-insensitive check works: `MuscleGroupPresets.isPreset("chest")` returns true
- [ ] Case-insensitive check works: `MuscleGroupPresets.isPreset("CHEST")` returns true
- [ ] Non-preset returns false: `MuscleGroupPresets.isPreset("Custom Group")` returns false
- [ ] Suggestions work: `MuscleGroupPresets.getPresetSuggestions("ch")` returns ["Chest"]
- [ ] Empty query returns all: `MuscleGroupPresets.getPresetSuggestions("")` returns all 8 presets

---

## Task 1.6: Update MainActivity with Compose Setup

**File Path:** `src/main/java/com/journal/ernie/MainActivity.kt`

**Purpose:** Transform MainActivity into the app entry point with Compose UI and navigation state management.

### Step-by-Step Instructions:

1. **Open the existing MainActivity.kt file:**
   - Current content is just: `class MainActivity { }`
   - We need to completely rewrite it

2. **Change class declaration:**
   - Change from: `class MainActivity { }`
   - To: `class MainActivity : ComponentActivity() { }`
   - `ComponentActivity` is the base class for Compose activities
   - Located in: `androidx.activity.ComponentActivity`

3. **Add all required imports at the top:**
   ```kotlin
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
   import androidx.compose.ui.platform.setContent
   import com.journal.ernie.ui.AppNavigation
   ```
   - Add these one by one, or let Android Studio auto-import as you type

4. **Create Screen sealed class (for navigation):**
   - Add this BEFORE the MainActivity class:
   ```kotlin
   sealed class Screen {
       object Home : Screen()
       object WorkoutList : Screen()
       object WorkoutSession : Screen()
   }
   ```
   - `sealed class` means all possible screens are defined here
   - Each screen is an `object` (singleton instance)
   - This prevents invalid navigation states

5. **Override onCreate method:**
   - Add this inside MainActivity class:
   ```kotlin
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       // Navigation state will go here
   }
   ```

6. **Add navigation state management:**
   - Inside `onCreate`, before `setContent`:
   ```kotlin
   var currentScreen by remember { mutableStateOf(Screen.Home) }
   ```
   - Wait, this won't work in `onCreate` - we need to move state to Compose
   - Actually, we'll manage state inside `setContent` block

7. **Add setContent block:**
   - Replace the comment with:
   ```kotlin
   setContent {
       // Navigation state
       var currentScreen by remember { mutableStateOf(Screen.Home) }
       
       // Navigation callback
       val onNavigateTo: (Screen) -> Unit = { screen ->
           currentScreen = screen
       }
       
       MaterialTheme {
           Surface(
               modifier = Modifier.fillMaxSize()
           ) {
               AppNavigation(
                   currentScreen = currentScreen,
                   onNavigateTo = onNavigateTo
               )
           }
       }
   }
   ```

8. **Complete structure:**
   - The file should now have:
     - Screen sealed class
     - MainActivity class extending ComponentActivity
     - onCreate method with setContent
     - Navigation state and callback
     - MaterialTheme and Surface wrapping AppNavigation

### Expected Code Structure:

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
import com.journal.ernie.ui.AppNavigation

sealed class Screen {
    object Home : Screen()
    object WorkoutList : Screen()
    object WorkoutSession : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            // Navigation state - remembers current screen across recompositions
            var currentScreen by remember { mutableStateOf(Screen.Home) }
            
            // Navigation callback - updates screen when called
            val onNavigateTo: (Screen) -> Unit = { screen ->
                currentScreen = screen
            }
            
            // Material3 theme wrapper
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Root navigation composable
                    AppNavigation(
                        currentScreen = currentScreen,
                        onNavigateTo = onNavigateTo
                    )
                }
            }
        }
    }
}
```

### Important Notes:

- **Why ComponentActivity?** This is the base class for activities that use Jetpack Compose. It provides the `setContent` function.
- **Why sealed class for Screen?** Sealed classes ensure type safety - you can only navigate to defined screens. The compiler will warn if you miss a case in `when` statements.
- **Why remember + mutableStateOf?** `remember` keeps state across recompositions. `mutableStateOf` makes the state observable - Compose will recompose when it changes.
- **Why MaterialTheme?** Provides theme colors, typography, and shapes to all child composables. Required for Material3 components.
- **Why Surface?** A Material3 container that applies theme colors. `fillMaxSize` makes it fill the entire screen.

### Testing Checklist:

After updating this file, verify:
- [ ] File compiles without errors
- [ ] All imports resolve correctly
- [ ] App runs without crashing (may show blank screen until AppNavigation is created)
- [ ] Screen sealed class has three objects: Home, WorkoutList, WorkoutSession
- [ ] Navigation state starts at Screen.Home

---

## Task 1.7: Create AppNavigation Composable

**File Path:** `src/main/java/com/journal/ernie/ui/AppNavigation.kt`

**Purpose:** Root navigation composable that routes to different screens based on current navigation state.

### Step-by-Step Instructions:

1. **Create the ui package directory (if not exists):**
   - Right-click on `com/journal/ernie/` package
   - Select "New" → "Package"
   - Name: `ui`

2. **Create the file:**
   - Right-click on `ui` package folder
   - Select "New" → "Kotlin Class/File"
   - Name: `AppNavigation`
   - Type: "File" (not class)

3. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.ui
   ```

4. **Add all required imports:**
   ```kotlin
   import androidx.compose.runtime.Composable
   import androidx.compose.material3.Text
   import androidx.compose.ui.Alignment
   import androidx.compose.ui.Modifier
   import androidx.compose.foundation.layout.Box
   import androidx.compose.foundation.layout.fillMaxSize
   import com.journal.ernie.Screen
   ```
   - We'll add more imports as we create actual screens in later phases

5. **Create the AppNavigation composable function:**
   - Function signature:
   ```kotlin
   @Composable
   fun AppNavigation(
       currentScreen: Screen,
       onNavigateTo: (Screen) -> Unit
   )
   ```
   - Parameters:
     - `currentScreen: Screen` - The currently active screen (from MainActivity)
     - `onNavigateTo: (Screen) -> Unit` - Callback to change screens (updates MainActivity state)

6. **Add navigation routing logic:**
   - Use `when` expression to route based on `currentScreen`:
   ```kotlin
   when (currentScreen) {
       is Screen.Home -> {
           // Placeholder for HomeScreen (will be created in Phase 2)
           Box(
               modifier = Modifier.fillMaxSize(),
               contentAlignment = Alignment.Center
           ) {
               Text("Home Screen")
           }
       }
       is Screen.WorkoutList -> {
           // Placeholder for WorkoutListScreen (will be created in Phase 4)
           Box(
               modifier = Modifier.fillMaxSize(),
               contentAlignment = Alignment.Center
           ) {
               Text("Workout List Screen")
           }
       }
       is Screen.WorkoutSession -> {
           // Placeholder for WorkoutSessionScreen (will be created in Phase 5)
           Box(
               modifier = Modifier.fillMaxSize(),
               contentAlignment = Alignment.Center
           ) {
               Text("Workout Session Screen")
           }
       }
   }
   ```

7. **Complete function structure:**
   - The function should be a simple `when` expression that shows different content based on screen

### Expected Code Structure:

```kotlin
package com.journal.ernie.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.journal.ernie.Screen

@Composable
fun AppNavigation(
    currentScreen: Screen,
    onNavigateTo: (Screen) -> Unit
) {
    when (currentScreen) {
        is Screen.Home -> {
            // Placeholder - HomeScreen will be created in Phase 2
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Home Screen")
            }
        }
        is Screen.WorkoutList -> {
            // Placeholder - WorkoutListScreen will be created in Phase 4
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Workout List Screen")
            }
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

- **Why when expression?** `when` is Kotlin's switch statement. It's exhaustive for sealed classes - compiler ensures all cases are handled.
- **Why is Screen.Home?** The `is` keyword checks the type. Since Screen is a sealed class, this pattern matches the specific screen object.
- **Why Box with fillMaxSize?** Box is a simple container. `fillMaxSize` makes it fill available space. `contentAlignment = Alignment.Center` centers the Text.
- **Why placeholders?** We're creating the structure now. Actual screens will be added in later phases. This lets us test navigation immediately.
- **Why pass onNavigateTo?** Even though we're not using it in placeholders, we'll need it when we create actual screens. They'll have buttons that call this callback.

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] App runs and shows "Home Screen" text centered
- [ ] All three screen cases are handled in when expression
- [ ] No compiler warnings about missing cases
- [ ] Imports resolve correctly (Screen from MainActivity)

### Testing Navigation (Optional):

To test navigation works, you can temporarily add a button in MainActivity's setContent:

```kotlin
// Temporary test - remove after verification
Button(onClick = { onNavigateTo(Screen.WorkoutList) }) {
    Text("Go to Workout List")
}
```

This should change the screen when clicked. Remove this test code after verifying.

---

## Task 1.8: Verify Package Structure

**Purpose:** Ensure all required directories exist before moving to Phase 2.

### Step-by-Step Instructions:

1. **Check data package:**
   - Navigate to: `src/main/java/com/journal/ernie/data/`
   - Verify these files exist:
     - `SetEntry.kt`
     - `Exercise.kt`
     - `MuscleGroup.kt`
     - `WorkoutSession.kt`
     - `MuscleGroupPresets.kt`

2. **Check ui package:**
   - Navigate to: `src/main/java/com/journal/ernie/ui/`
   - Verify this file exists:
     - `AppNavigation.kt`

3. **Check main package:**
   - Navigate to: `src/main/java/com/journal/ernie/`
   - Verify this file exists:
     - `MainActivity.kt`

4. **Check viewmodel package (will be used in Phase 3):**
   - Navigate to: `src/main/java/com/journal/ernie/viewmodel/`
   - This directory should exist (even if empty)
   - Will be populated in Phase 3

### Expected Final Structure:

```
src/main/java/com/journal/ernie/
├── MainActivity.kt
├── data/
│   ├── SetEntry.kt
│   ├── Exercise.kt
│   ├── MuscleGroup.kt
│   ├── WorkoutSession.kt
│   └── MuscleGroupPresets.kt
├── ui/
│   └── AppNavigation.kt
└── viewmodel/
    (empty for now)
```

### Verification Checklist:

- [ ] All 5 data model files exist in `data/` package
- [ ] AppNavigation.kt exists in `ui/` package
- [ ] MainActivity.kt exists in main package
- [ ] viewmodel package exists (can be empty)
- [ ] All files compile without errors
- [ ] App runs and displays "Home Screen" text

---

## Phase 1 Complete: Final Verification

After completing all tasks, run these comprehensive tests:

### Compilation Test:
- [ ] Project builds successfully (Build → Make Project)
- [ ] No red error underlines in any file
- [ ] All imports resolve correctly

### Data Model Tests:
- [ ] Can create SetEntry with defaults: `SetEntry()`
- [ ] Can create Exercise: `Exercise(name = "Test")`
- [ ] Can add set to exercise: `exercise.addSet(SetEntry())`
- [ ] Can create MuscleGroup: `MuscleGroup(name = "Chest")`
- [ ] Can add exercise to group: `group.addExercise(Exercise(name = "Test"))`
- [ ] Can create WorkoutSession: `WorkoutSession.createNew("Test Session")`
- [ ] Can add muscle group to session: `session.addMuscleGroup(MuscleGroup(name = "Chest"))`
- [ ] MuscleGroupPresets returns 8 presets
- [ ] MuscleGroupPresets.isPreset("Chest") returns true

### Navigation Test:
- [ ] App launches without crashing
- [ ] "Home Screen" text is displayed
- [ ] Navigation state is Screen.Home initially
- [ ] AppNavigation composable receives currentScreen parameter
- [ ] All three screen cases are handled in when expression

### Code Quality:
- [ ] All data classes use `data class` keyword
- [ ] All IDs use UUID generation
- [ ] All TODO comments are present
- [ ] Package declarations are correct
- [ ] No unused imports
- [ ] Code follows Kotlin conventions

---

## Common Issues & Solutions

### Issue: "Unresolved reference: ComponentActivity"
**Solution:** Add import: `import androidx.activity.ComponentActivity`
**Also check:** Ensure Compose dependencies are in build.gradle (but don't modify Gradle files per instructions)

### Issue: "Unresolved reference: Screen"
**Solution:** Ensure Screen sealed class is in MainActivity.kt and AppNavigation imports it: `import com.journal.ernie.Screen`

### Issue: "Cannot find symbol: UUID"
**Solution:** Add import: `import java.util.UUID`

### Issue: "Type mismatch: expected MutableList, found List"
**Solution:** Use `mutableListOf()` instead of `listOf()` when creating default empty lists

### Issue: App crashes on launch
**Solution:** 
- Check all imports are correct
- Verify MainActivity extends ComponentActivity
- Ensure setContent is called in onCreate
- Check that AppNavigation composable exists and compiles

### Issue: "Home Screen" text not visible
**Solution:**
- Check MaterialTheme and Surface are wrapping AppNavigation
- Verify Box has fillMaxSize modifier
- Check contentAlignment is set to Center

---

## Next Steps: Phase 2

After Phase 1 is complete and verified, you're ready for Phase 2:
- Create HomeScreen with 5 navigation boxes
- Wire up center box to navigate to Workout section
- Replace placeholder in AppNavigation with actual HomeScreen

**Phase 1 establishes the foundation. All future phases build on this structure.**

---

## Summary

Phase 1 creates:
- ✅ 4 data models (SetEntry, Exercise, MuscleGroup, WorkoutSession)
- ✅ 1 utility object (MuscleGroupPresets)
- ✅ Navigation structure (Screen sealed class, AppNavigation composable)
- ✅ Basic Compose setup (MainActivity with MaterialTheme)

**Total Files Created:** 6 new files + 1 updated file
**Total Lines of Code:** ~300-400 lines
**Time Estimate:** 2-3 hours for careful implementation

All files are ready for Room database integration in later phases (when Room dependencies are added to Gradle).

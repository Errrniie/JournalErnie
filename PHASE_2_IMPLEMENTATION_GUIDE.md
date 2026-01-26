# Phase 2: Home Screen - Detailed Implementation Guide

## Overview

This document provides step-by-step instructions for implementing Phase 2 of the Workout Tracking App. Phase 2 creates the Home Screen with 5 large navigation boxes, where the center box navigates to the Workout section and the other 4 boxes are placeholders.

**Goal:** Create a visually appealing home screen with 5 tappable navigation boxes, wire up the center box to navigate to the Workout section, and integrate it with the existing navigation system.

**Prerequisites:** 
- Phase 1 must be completed
- MainActivity.kt with navigation setup
- AppNavigation.kt with placeholder screens
- Understanding of Compose layouts (Column, Row, Grid)
- Basic knowledge of Material3 components (Card, Button, Text)

---

## Phase 2 Overview

Phase 2 consists of 2 main tasks:
1. Create HomeScreen composable with 5 navigation boxes
2. Wire up center box navigation and integrate with AppNavigation

**Estimated Time:** 1-2 hours
**Files to Create:** 1 new file
**Files to Modify:** 1 existing file

---

## Task 2.1: Create HomeScreen Composable

**File Path:** `src/main/java/com/journal/ernie/ui/HomeScreen.kt`

**Purpose:** Create a home screen that displays 5 large, tappable navigation boxes in a grid layout. The center box navigates to the Workout section, while the other 4 boxes are placeholders.

### Step-by-Step Instructions:

1. **Create the file:**
   - Right-click on `ui` package folder (should already exist from Phase 1)
   - Select "New" → "Kotlin Class/File"
   - Name: `HomeScreen`
   - Type: "File" (not class)

2. **Add package declaration:**
   ```kotlin
   package com.journal.ernie.ui
   ```

3. **Add all required imports:**
   ```kotlin
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
   import androidx.compose.foundation.layout.width
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
   ```
   - Add these imports as you need them, or let Android Studio auto-import

4. **Create the HomeScreen composable function:**
   - Function signature:
   ```kotlin
   @Composable
   fun HomeScreen(
       onNavigateTo: (Screen) -> Unit
   )
   ```
   - Parameter:
     - `onNavigateTo: (Screen) -> Unit` - Callback function to navigate to different screens
     - This will be called when user taps the center box

5. **Design the layout structure:**
   - The screen should have 5 boxes arranged in a grid
   - Layout options:
     - **Option A:** 2 rows, 3 columns (center box in middle of second row)
     - **Option B:** 3 rows, 2 columns (center box in middle row, spanning both columns)
     - **Option C:** Custom grid with center box larger
   - **Recommended:** Use a Column with 2 Rows, where:
     - First row: 2 boxes (side by side)
     - Second row: 1 center box (centered, larger)
     - Third row: 2 boxes (side by side)

6. **Create a reusable NavigationBox composable:**
   - This will be a helper function to create each box
   - Function signature:
   ```kotlin
   @Composable
   fun NavigationBox(
       title: String,
       subtitle: String? = null,
       enabled: Boolean = true,
       onClick: () -> Unit,
       modifier: Modifier = Modifier
   )
   ```
   - Parameters:
     - `title: String` - Main text displayed in the box
     - `subtitle: String?` - Optional subtitle (e.g., "Coming Soon")
     - `enabled: Boolean` - Whether the box is clickable
     - `onClick: () -> Unit` - Click handler
     - `modifier: Modifier` - For sizing and styling

7. **Implement NavigationBox:**
   - Use a `Card` component (Material3)
   - Inside Card, use a `Box` with `fillMaxSize` modifier
   - Center content using `contentAlignment = Alignment.Center`
   - Add `Column` inside Box for vertical text layout
   - Display `title` as main text (large, bold)
   - Display `subtitle` if provided (smaller, gray)
   - Add `clickable` modifier (only if enabled)
   - Style disabled boxes differently (lower opacity, grayed out)

8. **Implement HomeScreen layout:**
   - Use `Column` as root container with `fillMaxSize` and padding
   - Add spacing between rows using `Spacer` or `Arrangement.spacedBy`
   - Create first row with `Row` containing 2 boxes:
     - Box 1: "Placeholder 1" (disabled)
     - Box 2: "Placeholder 2" (disabled)
   - Create second row with centered box:
     - Center box: "Workout" (enabled, navigates to WorkoutList)
   - Create third row with `Row` containing 2 boxes:
     - Box 4: "Placeholder 3" (disabled)
     - Box 5: "Placeholder 4" (disabled)

9. **Add navigation callback:**
   - Center box's `onClick` should call: `onNavigateTo(Screen.WorkoutList)`
   - Other boxes can have empty onClick or show a toast (optional)

10. **Style the boxes:**
    - Each box should be large and prominent
    - Recommended size: ~150-200dp height, fill available width
    - Use Material3 colors from theme
    - Center box can be slightly larger or have different color
    - Add elevation to cards for depth
    - Use rounded corners (CardDefaults.cardShape)

### Expected Code Structure:

```kotlin
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
import androidx.compose.foundation.layout.width
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
```

### Important Notes:

- **Why Card component?** Cards provide elevation, rounded corners, and Material3 styling automatically. They're perfect for clickable navigation items.
- **Why separate NavigationBox composable?** Reusability - we create 5 boxes with the same structure but different content. This follows DRY (Don't Repeat Yourself) principle.
- **Why enabled/disabled state?** Placeholder boxes should look different and not be clickable. This provides clear visual feedback to users.
- **Why weight(1f) in Row?** This makes boxes share available width equally. Each box gets 50% of the row width.
- **Why fillMaxWidth(0.9f) for center box?** Makes it slightly narrower than full width, creating visual emphasis and better spacing.
- **Why MaterialTheme colors?** Using theme colors ensures the app adapts to light/dark mode automatically and follows Material Design guidelines.
- **Why Arrangement.spacedBy?** Provides consistent spacing between elements without manual Spacer components.

### Layout Visualization:

```
┌─────────────────────────────────────┐
│  [Placeholder 1]  [Placeholder 2]  │  ← Row 1
│                                     │
│        [   Workout   ]             │  ← Row 2 (centered, larger)
│                                     │
│  [Placeholder 3]  [Placeholder 4]  │  ← Row 3
└─────────────────────────────────────┘
```

### Testing Checklist:

After creating this file, verify:
- [ ] File compiles without errors
- [ ] All 5 boxes are visible on screen
- [ ] Center box is larger or visually distinct
- [ ] Placeholder boxes show "Coming Soon" subtitle
- [ ] Placeholder boxes are visually disabled (grayed out, lower opacity)
- [ ] Center box has "Workout" title and "Track your workouts" subtitle
- [ ] Layout is centered and has proper spacing
- [ ] Boxes have rounded corners and elevation
- [ ] Text is readable and properly aligned

---

## Task 2.2: Integrate HomeScreen with AppNavigation

**File Path:** `src/main/java/com/journal/ernie/ui/AppNavigation.kt`

**Purpose:** Replace the placeholder "Home Screen" text with the actual HomeScreen composable.

### Step-by-Step Instructions:

1. **Open AppNavigation.kt:**
   - File should already exist from Phase 1
   - Current content has placeholder: `Text("Home Screen")`

2. **Add import for HomeScreen:**
   - Add to imports section:
   ```kotlin
   import com.journal.ernie.ui.HomeScreen
   ```
   - Note: Since both are in the same package, you might not need this import, but it's good practice

3. **Replace placeholder in Screen.Home case:**
   - Find the `is Screen.Home ->` case in the `when` expression
   - Replace the entire `Box { Text("Home Screen") }` block
   - With: `HomeScreen(onNavigateTo = onNavigateTo)`
   - This passes the navigation callback to HomeScreen

4. **Verify the integration:**
   - The `when` expression should now look like:
   ```kotlin
   when (currentScreen) {
       is Screen.Home -> {
           HomeScreen(onNavigateTo = onNavigateTo)
       }
       is Screen.WorkoutList -> {
           // Placeholder remains for now
           Box(
               modifier = Modifier.fillMaxSize(),
               contentAlignment = Alignment.Center
           ) {
               Text("Workout List Screen")
           }
       }
       is Screen.WorkoutSession -> {
           // Placeholder remains for now
           Box(
               modifier = Modifier.fillMaxSize(),
               contentAlignment = Alignment.Center
           ) {
               Text("Workout Session Screen")
           }
       }
   }
   ```

5. **Remove unused imports (if any):**
   - If you removed the Box/Text from Home case, you might have unused imports
   - Android Studio will show warnings - clean them up
   - But keep Box and Text imports since they're still used in other cases

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

@Composable
fun AppNavigation(
    currentScreen: Screen,
    onNavigateTo: (Screen) -> Unit
) {
    when (currentScreen) {
        is Screen.Home -> {
            HomeScreen(onNavigateTo = onNavigateTo)
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

- **Why pass onNavigateTo?** HomeScreen needs the callback to navigate when center box is tapped. This maintains the navigation flow.
- **Why keep placeholders?** WorkoutListScreen and WorkoutSessionScreen will be created in later phases. Keeping placeholders allows testing navigation without errors.
- **Why same package?** HomeScreen and AppNavigation are in the same package, so they can reference each other directly.

### Testing Checklist:

After updating AppNavigation, verify:
- [ ] File compiles without errors
- [ ] App launches and shows HomeScreen (not placeholder text)
- [ ] All 5 boxes are visible
- [ ] Tapping center "Workout" box navigates to "Workout List Screen" placeholder
- [ ] Placeholder boxes don't navigate (or show appropriate feedback)
- [ ] Navigation works smoothly

---

## Task 2.3: Test Navigation Flow

**Purpose:** Verify that navigation from HomeScreen to WorkoutList works correctly.

### Step-by-Step Testing:

1. **Launch the app:**
   - Run the app on an emulator or device
   - App should start on HomeScreen (not blank screen)

2. **Verify HomeScreen displays:**
   - Check that 5 boxes are visible
   - Verify center box says "Workout"
   - Verify other boxes say "Placeholder" and "Coming Soon"

3. **Test center box navigation:**
   - Tap the center "Workout" box
   - Screen should change to show "Workout List Screen" text (placeholder)
   - This confirms navigation callback works

4. **Test back navigation (if implemented):**
   - If you have a back button, test that it returns to HomeScreen
   - If not, that's OK - back navigation will be added in later phases

5. **Test placeholder boxes:**
   - Tap a placeholder box
   - It should not navigate (or show appropriate disabled state)
   - Visual feedback should indicate it's disabled

### Expected Behavior:

- **On app launch:** HomeScreen with 5 boxes
- **After tapping "Workout" box:** Screen changes to show "Workout List Screen" placeholder
- **Placeholder boxes:** No navigation, visual indication of disabled state

### Testing Checklist:

- [ ] App launches successfully
- [ ] HomeScreen is displayed on launch
- [ ] All 5 boxes are visible and properly styled
- [ ] Center box tap navigates to WorkoutList screen
- [ ] Placeholder boxes are visually disabled
- [ ] No crashes or errors in logcat
- [ ] Layout looks good in both portrait and landscape (optional check)

---

## Phase 2 Complete: Final Verification

After completing all tasks, run these comprehensive tests:

### Visual Verification:
- [ ] HomeScreen displays 5 boxes in correct layout
- [ ] Center box is visually distinct (larger or different color)
- [ ] Placeholder boxes are grayed out/disabled
- [ ] Text is readable and properly sized
- [ ] Spacing between boxes is consistent
- [ ] Cards have proper elevation and rounded corners

### Functional Verification:
- [ ] Center box is clickable and navigates
- [ ] Placeholder boxes don't navigate (or show feedback)
- [ ] Navigation callback works correctly
- [ ] App doesn't crash on navigation
- [ ] Screen transitions are smooth

### Code Quality:
- [ ] HomeScreen.kt compiles without errors
- [ ] AppNavigation.kt compiles without errors
- [ ] No unused imports
- [ ] Code follows Kotlin conventions
- [ ] NavigationBox is reusable and well-structured
- [ ] Comments are added where needed

---

## Common Issues & Solutions

### Issue: "Unresolved reference: HomeScreen"
**Solution:** 
- Ensure HomeScreen.kt is in the same package as AppNavigation.kt (`com.journal.ernie.ui`)
- Check that HomeScreen composable function is marked with `@Composable`
- Verify file was created correctly and compiles independently

### Issue: Boxes are too small or too large
**Solution:**
- Adjust `height` modifier values (try 120.dp to 200.dp)
- Adjust `fillMaxWidth(0.9f)` value for center box (try 0.8f to 1.0f)
- Use `weight(1f)` in Row to make boxes share space equally

### Issue: Boxes overlap or spacing is wrong
**Solution:**
- Check `Arrangement.spacedBy(16.dp)` is applied to Row/Column
- Verify `padding(16.dp)` on root Column
- Ensure boxes have proper modifiers (weight, height)

### Issue: Center box doesn't navigate
**Solution:**
- Verify `onClick = { onNavigateTo(Screen.WorkoutList) }` is correct
- Check that `onNavigateTo` parameter is passed from AppNavigation
- Verify Screen.WorkoutList exists in Screen sealed class
- Check logcat for any errors

### Issue: Placeholder boxes are clickable
**Solution:**
- Ensure `enabled = false` is passed to placeholder NavigationBox calls
- Verify `clickable` modifier is only applied when `enabled == true`
- Check that disabled boxes have different visual styling

### Issue: Text is cut off or not visible
**Solution:**
- Increase padding inside NavigationBox (try 24.dp or more)
- Check text color contrasts with background
- Verify text style sizes are appropriate
- Ensure Box has `fillMaxSize()` modifier

### Issue: Layout looks wrong on different screen sizes
**Solution:**
- Use `weight()` instead of fixed widths
- Use `fillMaxWidth()` with fractions instead of fixed dp values
- Test on different screen sizes/emulators
- Consider using `LazyVerticalGrid` for more responsive layout (advanced)

---

## Design Considerations

### Layout Options:

**Option 1: Current Design (Recommended)**
- 2 rows of 2 boxes, 1 centered box
- Simple, clear hierarchy
- Center box is naturally emphasized

**Option 2: Grid Layout**
- 3x2 grid (6 boxes, hide one or make center span)
- More uniform appearance
- Requires LazyVerticalGrid (more complex)

**Option 3: Single Column**
- 5 boxes stacked vertically
- Simplest layout
- Less visual interest

**Recommendation:** Use Option 1 (current design) for best balance of simplicity and visual appeal.

### Styling Options:

**Color Scheme:**
- Center box: `primaryContainer` (enabled)
- Placeholder boxes: `surfaceVariant` with reduced opacity (disabled)
- Text: `onPrimaryContainer` for enabled, `onSurfaceVariant` for disabled

**Sizing:**
- Regular boxes: 150.dp height
- Center box: 180.dp height (20% larger)
- Width: Use `weight(1f)` for equal distribution

**Elevation:**
- Enabled boxes: 4.dp elevation
- Disabled boxes: 2.dp elevation
- Creates depth and indicates interactivity

---

## Next Steps: Phase 3

After Phase 2 is complete and verified, you're ready for Phase 3:
- Create WorkoutViewModel with state management
- Implement session management functions
- Implement timer functions
- This will provide the data layer for workout screens

**Phase 2 establishes the home screen and navigation. Phase 3 will add the business logic layer.**

---

## Summary

Phase 2 creates:
- ✅ HomeScreen composable with 5 navigation boxes
- ✅ NavigationBox reusable component
- ✅ Integration with AppNavigation
- ✅ Working navigation from Home to Workout section

**Total Files Created:** 1 new file (HomeScreen.kt)
**Total Files Modified:** 1 file (AppNavigation.kt)
**Total Lines of Code:** ~200-250 lines
**Time Estimate:** 1-2 hours for careful implementation

The home screen is now functional and ready for users to navigate to the Workout section. Placeholder boxes are ready to be wired up in future phases when those features are implemented.

package com.journal.ernie.data

/**
 * Provides preset muscle group names and utility functions for suggestions and validation.
 * 
 * This singleton object contains a predefined list of common muscle group names
 * that can be used for quick selection or as suggestions when users are typing.
 */
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
    
    /**
     * Returns all available preset muscle group names.
     * 
     * @return A list of all preset muscle group names.
     */
    fun getAllPresets(): List<String> {
        return PRESET_GROUPS.toList()
    }
    
    /**
     * Checks if a given name matches any preset muscle group (case-insensitive).
     * 
     * @param name The name to check.
     * @return true if the name matches a preset, false otherwise.
     */
    fun isPreset(name: String): Boolean {
        return PRESET_GROUPS.any { 
            it.equals(name, ignoreCase = true) 
        }
    }
    
    /**
     * Returns preset muscle groups that contain the query string (case-insensitive).
     * 
     * Useful for providing suggestions as the user types.
     * 
     * @param query The search query string.
     * @return A list of preset names that contain the query.
     */
    fun getPresetSuggestions(query: String): List<String> {
        return PRESET_GROUPS.filter { 
            it.contains(query, ignoreCase = true) 
        }
    }
}

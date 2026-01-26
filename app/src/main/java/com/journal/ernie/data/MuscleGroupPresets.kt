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

package com.journal.ernie.data

/**
 * Represents an exercise within a muscle group.
 * 
 * An exercise contains a name and a list of set entries. Each exercise has a unique
 * identifier and provides methods to manage its sets.
 * 
 * TODO: Add @Entity annotation when Room is integrated
 * TODO: Add @PrimaryKey annotation to id field
 * TODO: Convert sets to @Relation or separate entity
 * 
 * @param id Unique identifier for the exercise (UUID string).
 * @param name The name of the exercise (e.g., "Bench Press", "Squats").
 * @param sets List of set entries performed for this exercise (default: empty list).
 */
// TODO: Add @Entity annotation when Room is integrated
data class Exercise(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val sets: MutableList<SetEntry> = mutableListOf()
) {
    /**
     * Adds a set entry to this exercise.
     * 
     * @param set The set entry to add.
     */
    fun addSet(set: SetEntry) {
        sets.add(set)
    }
    
    /**
     * Removes a set entry by its index.
     * 
     * @param index The zero-based index of the set to remove.
     */
    fun removeSet(index: Int) {
        if (index in sets.indices) {
            sets.removeAt(index)
        }
    }
    
    /**
     * Removes a set entry by its unique ID.
     * 
     * @param id The unique identifier of the set to remove.
     * @return true if a set was removed, false otherwise.
     */
    fun removeSetById(id: String): Boolean {
        return sets.removeAll { it.id == id }
    }
    
    /**
     * Gets the total number of sets for this exercise.
     * 
     * @return The number of set entries.
     */
    fun getSetCount(): Int = sets.size
}

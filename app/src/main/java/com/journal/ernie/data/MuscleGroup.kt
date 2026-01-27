package com.journal.ernie.data

/**
 * Represents a muscle group within a workout session.
 * 
 * A muscle group contains a name and a list of exercises. Each muscle group has a unique
 * identifier and provides methods to manage its exercises.
 * 
 * TODO: Add @Entity annotation when Room is integrated
 * TODO: Add @PrimaryKey annotation to id field
 * TODO: Convert exercises to @Relation or separate entity
 * 
 * @param id Unique identifier for the muscle group (UUID string).
 * @param name The name of the muscle group (e.g., "Chest", "Back", "Legs").
 * @param exercises List of exercises in this muscle group (default: empty list).
 */
// TODO: Add @Entity annotation when Room is integrated
data class MuscleGroup(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val exercises: MutableList<Exercise> = mutableListOf()
) {
    /**
     * Adds an exercise to this muscle group.
     * 
     * @param exercise The exercise to add.
     */
    fun addExercise(exercise: Exercise) {
        exercises.add(exercise)
    }
    
    /**
     * Removes an exercise by its unique ID.
     * 
     * @param id The unique identifier of the exercise to remove.
     * @return true if an exercise was removed, false otherwise.
     */
    fun removeExercise(id: String): Boolean {
        return exercises.removeAll { it.id == id }
    }
    
    /**
     * Finds an exercise by its unique ID.
     * 
     * @param id The unique identifier of the exercise.
     * @return The exercise if found, null otherwise.
     */
    fun findExerciseById(id: String): Exercise? {
        return exercises.find { it.id == id }
    }
    
    /**
     * Gets the total number of exercises in this muscle group.
     * 
     * @return The number of exercises.
     */
    fun getExerciseCount(): Int = exercises.size
}

package com.journal.ernie.data

/**
 * Represents a complete workout session.
 * 
 * A workout session contains a name, creation date, and a list of muscle groups.
 * Each session has a unique identifier and provides methods to manage its muscle groups.
 * 
 * TODO: Add @Entity annotation when Room is integrated
 * TODO: Add @PrimaryKey annotation to id field
 * TODO: Convert muscleGroups to @Relation or separate entity
 * 
 * @param id Unique identifier for the workout session (UUID string).
 * @param name The name of the workout session.
 * @param date Timestamp when the session was created (default: current time).
 * @param muscleGroups List of muscle groups in this session (default: empty list).
 */
// TODO: Add @Entity annotation when Room is integrated
data class WorkoutSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val date: Long = System.currentTimeMillis(),
    val muscleGroups: MutableList<MuscleGroup> = mutableListOf()
) {
    /**
     * Adds a muscle group to this workout session.
     * 
     * @param group The muscle group to add.
     */
    fun addMuscleGroup(group: MuscleGroup) {
        muscleGroups.add(group)
    }
    
    /**
     * Removes a muscle group by its unique ID.
     * 
     * @param id The unique identifier of the muscle group to remove.
     * @return true if a muscle group was removed, false otherwise.
     */
    fun removeMuscleGroup(id: String): Boolean {
        return muscleGroups.removeAll { it.id == id }
    }
    
    /**
     * Finds a muscle group by its unique ID.
     * 
     * @param id The unique identifier of the muscle group.
     * @return The muscle group if found, null otherwise.
     */
    fun findMuscleGroupById(id: String): MuscleGroup? {
        return muscleGroups.find { it.id == id }
    }
    
    /**
     * Gets the total number of exercises across all muscle groups in this session.
     * 
     * @return The total number of exercises.
     */
    fun getTotalExerciseCount(): Int {
        return muscleGroups.sumOf { it.exercises.size }
    }
    
    companion object {
        /**
         * Creates a new workout session with the given name.
         * 
         * The session is initialized with a new UUID, current timestamp, and an empty
         * list of muscle groups.
         * 
         * @param name The name of the workout session.
         * @return A new WorkoutSession instance.
         */
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

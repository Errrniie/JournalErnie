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

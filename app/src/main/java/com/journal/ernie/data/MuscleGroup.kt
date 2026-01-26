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

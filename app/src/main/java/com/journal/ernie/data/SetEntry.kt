package com.journal.ernie.data

/**
 * Represents a single set entry within an exercise.
 * 
 * A set entry contains the number of repetitions, weight used, and an optional comment.
 * Each set has a unique identifier generated automatically.
 * 
 * TODO: Add @Entity annotation when Room is integrated
 * TODO: Add @PrimaryKey annotation to id field
 * TODO: Consider changing id type from String to Long for Room compatibility
 * 
 * @param id Unique identifier for the set entry (UUID string).
 * @param reps Number of repetitions performed (default: 0).
 * @param weight Weight used in kilograms (default: 0.0).
 * @param comment Optional comment or notes about the set (default: null).
 */
// TODO: Add @Entity annotation when Room is integrated
data class SetEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    var reps: Int = 0,
    var weight: Float = 0.0f,
    var comment: String? = null
)

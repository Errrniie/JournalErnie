package com.journal.ernie.data

// TODO: Add @Entity annotation when Room is integrated
data class SetEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    var reps: Int = 0,
    var weight: Float = 0.0f,
    var comment: String? = null
)

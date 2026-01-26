package com.journal.ernie.data

// TODO: Add @Entity annotation when Room is integrated
data class Exercise(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val sets: MutableList<SetEntry> = mutableListOf()
) {
    fun addSet(set: SetEntry) {
        sets.add(set)
    }
    
    fun removeSet(index: Int) {
        if (index in sets.indices) {
            sets.removeAt(index)
        }
    }
    
    fun removeSetById(id: String): Boolean {
        return sets.removeAll { it.id == id }
    }
    
    fun getSetCount(): Int = sets.size
}

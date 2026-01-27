package com.journal.ernie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.journal.ernie.data.Exercise
import com.journal.ernie.data.MuscleGroup
import com.journal.ernie.data.SetEntry
import com.journal.ernie.data.WorkoutSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutViewModel : ViewModel() {
    // Private mutable state flows
    private val _currentSession = MutableStateFlow<WorkoutSession?>(null)
    private val _allSessions = MutableStateFlow<List<WorkoutSession>>(emptyList())
    private val _timerState = MutableStateFlow<TimerState>(TimerState.initial())
    
    // Timer job for managing timer coroutine
    private var timerJob: Job? = null
    
    // Public read-only state flows
    val currentSession: StateFlow<WorkoutSession?> = _currentSession.asStateFlow()
    val allSessions: StateFlow<List<WorkoutSession>> = _allSessions.asStateFlow()
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    
    init {
        loadSessions()
    }
    
    // Validation Functions
    
    private fun validateSessionName(name: String): Boolean {
        val trimmed = name.trim()
        return trimmed.isNotEmpty() && trimmed.length <= 50
    }
    
    private fun validateMuscleGroupName(name: String): Boolean {
        val trimmed = name.trim()
        return trimmed.isNotEmpty() && trimmed.length <= 50
    }
    
    private fun validateExerciseName(name: String): Boolean {
        val trimmed = name.trim()
        return trimmed.isNotEmpty() && trimmed.length <= 50
    }
    
    private fun validateSetData(reps: Int, weight: Float): Boolean {
        return reps >= 0 && reps <= 1000 && weight >= 0f && weight <= 1000f
    }
    
    // Session Management Functions
    
    fun loadSessions() {
        // TODO: Load from Room database when implemented
        // For now, sessions are stored in memory only
        // This function is called in init block
    }
    
    fun createNewSession(name: String) {
        viewModelScope.launch {
            if (!validateSessionName(name)) {
                return@launch
            }
            val newSession = WorkoutSession.createNew(name.trim())
            
            // Add to all sessions list
            _allSessions.update { currentList ->
                currentList + newSession
            }
            
            // Set as current session
            _currentSession.value = newSession
            
            // Reset timer
            _timerState.value = TimerState.initial()
            
            // TODO: Save to Room database when implemented
        }
    }
    
    fun selectSession(id: String) {
        viewModelScope.launch {
            val session = _allSessions.value.find { it.id == id }
            
            if (session != null) {
                _currentSession.value = session
                _timerState.value = TimerState.initial()
            } else {
                // Session not found - could log error or handle gracefully
                _currentSession.value = null
            }
            
            // TODO: Load full session data from Room database when implemented
        }
    }
    
    fun clearCurrentSession() {
        _currentSession.value = null
        _timerState.value = TimerState.initial()
    }
    
    // Muscle Group CRUD Functions
    
    fun addMuscleGroup(name: String) {
        viewModelScope.launch {
            val session = _currentSession.value ?: return@launch
            if (!validateMuscleGroupName(name)) {
                return@launch
            }
            
            val newGroup = MuscleGroup(name = name.trim())
            session.addMuscleGroup(newGroup)
            
            // Update current session
            _currentSession.value = session
            
            // Update all sessions list
            _allSessions.update { sessions ->
                sessions.map { if (it.id == session.id) session else it }
            }
            
            // TODO: Save to Room database when implemented
        }
    }
    
    fun removeMuscleGroup(groupId: String) {
        viewModelScope.launch {
            val session = _currentSession.value ?: return@launch
            
            session.removeMuscleGroup(groupId)
            
            // Update current session
            _currentSession.value = session
            
            // Update all sessions list
            _allSessions.update { sessions ->
                sessions.map { if (it.id == session.id) session else it }
            }
            
            // TODO: Delete from Room database when implemented
        }
    }
    
    fun findMuscleGroupById(groupId: String): MuscleGroup? {
        return _currentSession.value?.findMuscleGroupById(groupId)
    }
    
    // Exercise CRUD Functions
    
    fun addExercise(groupId: String, exerciseName: String) {
        viewModelScope.launch {
            val session = _currentSession.value ?: return@launch
            val group = session.findMuscleGroupById(groupId) ?: return@launch
            if (!validateExerciseName(exerciseName)) {
                return@launch
            }
            
            val newExercise = Exercise(name = exerciseName.trim())
            group.addExercise(newExercise)
            
            // Update current session
            _currentSession.value = session
            
            // Update all sessions list
            _allSessions.update { sessions ->
                sessions.map { if (it.id == session.id) session else it }
            }
            
            // TODO: Save to Room database when implemented
        }
    }
    
    fun removeExercise(groupId: String, exerciseId: String) {
        viewModelScope.launch {
            val session = _currentSession.value ?: return@launch
            val group = session.findMuscleGroupById(groupId) ?: return@launch
            
            group.removeExercise(exerciseId)
            
            // Update current session
            _currentSession.value = session
            
            // Update all sessions list
            _allSessions.update { sessions ->
                sessions.map { if (it.id == session.id) session else it }
            }
            
            // TODO: Delete from Room database when implemented
        }
    }
    
    fun findExerciseById(groupId: String, exerciseId: String): Exercise? {
        val group = findMuscleGroupById(groupId) ?: return null
        return group.findExerciseById(exerciseId)
    }
    
    // Set CRUD Functions
    
    fun addSet(groupId: String, exerciseId: String) {
        viewModelScope.launch {
            val session = _currentSession.value ?: return@launch
            val group = session.findMuscleGroupById(groupId) ?: return@launch
            val exercise = group.findExerciseById(exerciseId) ?: return@launch
            
            val newSet = SetEntry()
            exercise.addSet(newSet)
            
            // Update current session
            _currentSession.value = session
            
            // Update all sessions list
            _allSessions.update { sessions ->
                sessions.map { if (it.id == session.id) session else it }
            }
            
            // TODO: Save to Room database when implemented
        }
    }
    
    fun removeSet(groupId: String, exerciseId: String, setIndex: Int) {
        viewModelScope.launch {
            val session = _currentSession.value ?: return@launch
            val group = session.findMuscleGroupById(groupId) ?: return@launch
            val exercise = group.findExerciseById(exerciseId) ?: return@launch
            
            exercise.removeSet(setIndex)
            
            // Update current session
            _currentSession.value = session
            
            // Update all sessions list
            _allSessions.update { sessions ->
                sessions.map { if (it.id == session.id) session else it }
            }
            
            // TODO: Delete from Room database when implemented
        }
    }
    
    fun updateSet(
        groupId: String,
        exerciseId: String,
        setIndex: Int,
        reps: Int,
        weight: Float,
        comment: String?
    ) {
        viewModelScope.launch {
            val session = _currentSession.value ?: return@launch
            val group = session.findMuscleGroupById(groupId) ?: return@launch
            val exercise = group.findExerciseById(exerciseId) ?: return@launch
            
            // Validate index
            if (setIndex !in exercise.sets.indices) return@launch
            
            // Validate set data
            if (!validateSetData(reps, weight)) {
                return@launch
            }
            
            // Update set
            val set = exercise.sets[setIndex]
            set.reps = reps
            set.weight = weight
            set.comment = comment
            
            // Update current session
            _currentSession.value = session
            
            // Update all sessions list
            _allSessions.update { sessions ->
                sessions.map { if (it.id == session.id) session else it }
            }
            
            // TODO: Update in Room database when implemented
        }
    }
    
    fun removeSetById(groupId: String, exerciseId: String, setId: String) {
        viewModelScope.launch {
            val session = _currentSession.value ?: return@launch
            val group = session.findMuscleGroupById(groupId) ?: return@launch
            val exercise = group.findExerciseById(exerciseId) ?: return@launch
            
            exercise.removeSetById(setId)
            
            // Update current session
            _currentSession.value = session
            
            // Update all sessions list
            _allSessions.update { sessions ->
                sessions.map { if (it.id == session.id) session else it }
            }
            
            // TODO: Delete from Room database when implemented
        }
    }
    
    // Timer Functions
    
    fun startTimer() {
        // Don't start if already running
        if (_timerState.value.isRunning) return
        
        // Cancel any existing job
        timerJob?.cancel()
        
        // Start new timer
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000) // Wait 1 second
                _timerState.update { currentState ->
                    currentState.copy(
                        elapsedTimeSeconds = currentState.elapsedTimeSeconds + 1,
                        isRunning = true
                    )
                }
            }
        }
    }
    
    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        
        _timerState.update { currentState ->
            currentState.copy(isRunning = false)
        }
    }
    
    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        
        _timerState.value = TimerState.initial()
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

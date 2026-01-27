package com.journal.ernie.viewmodel

// Android
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

// Kotlin Coroutines
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Project
import com.journal.ernie.data.Exercise
import com.journal.ernie.data.MuscleGroup
import com.journal.ernie.data.SetEntry
import com.journal.ernie.data.WorkoutSession

/**
 * ViewModel for managing workout sessions, exercises, sets, and timer state.
 * 
 * This ViewModel provides a reactive interface for the workout tracking app,
 * managing all workout-related data in memory. State survives configuration
 * changes but does not persist across app restarts (Room integration pending).
 */
class WorkoutViewModel : ViewModel() {
    // Private mutable state flows
    private val _currentSession = MutableStateFlow<WorkoutSession?>(null)
    private val _allSessions = MutableStateFlow<List<WorkoutSession>>(emptyList())
    private val _timerState = MutableStateFlow<TimerState>(TimerState.initial())
    
    // Timer job for managing timer coroutine
    private var timerJob: Job? = null
    
    /**
     * The currently active workout session, or null if no session is selected.
     */
    val currentSession: StateFlow<WorkoutSession?> = _currentSession.asStateFlow()
    
    /**
     * List of all workout sessions created by the user.
     */
    val allSessions: StateFlow<List<WorkoutSession>> = _allSessions.asStateFlow()
    
    /**
     * Current state of the workout timer (elapsed time and running status).
     */
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
    
    /**
     * Loads all workout sessions from storage.
     * 
     * Currently loads from in-memory storage only. This function is called
     * automatically during ViewModel initialization.
     * 
     * TODO: Load from Room database when implemented
     */
    fun loadSessions() {
        // TODO: Load from Room database when implemented
        // For now, sessions are stored in memory only
        // This function is called in init block
    }
    
    /**
     * Creates a new workout session with the given name.
     * 
     * The session name is validated (non-empty, max 50 characters) and trimmed
     * before creation. The new session is automatically set as the current session
     * and the timer is reset.
     * 
     * @param name The name of the workout session. Must be non-empty and 50 characters or less.
     */
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
    
    /**
     * Selects a workout session by ID as the current active session.
     * 
     * If the session is found, it becomes the current session and the timer
     * is reset. If not found, the current session is cleared.
     * 
     * @param id The unique identifier of the session to select.
     */
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
    
    /**
     * Clears the current workout session and resets the timer.
     * 
     * This is useful when navigating away from a workout session without
     * selecting a new one.
     */
    fun clearCurrentSession() {
        _currentSession.value = null
        _timerState.value = TimerState.initial()
    }
    
    // Muscle Group CRUD Functions
    
    /**
     * Adds a new muscle group to the current workout session.
     * 
     * The muscle group name is validated (non-empty, max 50 characters) and trimmed.
     * If no current session exists, the operation is silently ignored.
     * 
     * @param name The name of the muscle group. Must be non-empty and 50 characters or less.
     */
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
    
    /**
     * Removes a muscle group from the current workout session.
     * 
     * If no current session exists or the muscle group is not found,
     * the operation is silently ignored.
     * 
     * @param groupId The unique identifier of the muscle group to remove.
     */
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
    
    /**
     * Finds a muscle group by its ID in the current session.
     * 
     * @param groupId The unique identifier of the muscle group.
     * @return The muscle group if found, null otherwise.
     */
    fun findMuscleGroupById(groupId: String): MuscleGroup? {
        return _currentSession.value?.findMuscleGroupById(groupId)
    }
    
    // Exercise CRUD Functions
    
    /**
     * Adds a new exercise to a muscle group in the current workout session.
     * 
     * The exercise name is validated (non-empty, max 50 characters) and trimmed.
     * If no current session exists or the muscle group is not found,
     * the operation is silently ignored.
     * 
     * @param groupId The unique identifier of the muscle group.
     * @param exerciseName The name of the exercise. Must be non-empty and 50 characters or less.
     */
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
    
    /**
     * Removes an exercise from a muscle group in the current workout session.
     * 
     * If no current session exists, the muscle group is not found, or the exercise
     * is not found, the operation is silently ignored.
     * 
     * @param groupId The unique identifier of the muscle group.
     * @param exerciseId The unique identifier of the exercise to remove.
     */
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
    
    /**
     * Finds an exercise by its ID within a muscle group.
     * 
     * @param groupId The unique identifier of the muscle group.
     * @param exerciseId The unique identifier of the exercise.
     * @return The exercise if found, null otherwise.
     */
    fun findExerciseById(groupId: String, exerciseId: String): Exercise? {
        val group = findMuscleGroupById(groupId) ?: return null
        return group.findExerciseById(exerciseId)
    }
    
    // Set CRUD Functions
    
    /**
     * Adds a new set entry to an exercise in the current workout session.
     * 
     * The new set is initialized with default values (0 reps, 0.0 weight, no comment).
     * If no current session exists, the muscle group is not found, or the exercise
     * is not found, the operation is silently ignored.
     * 
     * @param groupId The unique identifier of the muscle group.
     * @param exerciseId The unique identifier of the exercise.
     */
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
    
    /**
     * Removes a set entry from an exercise by its index.
     * 
     * If no current session exists, the muscle group is not found, the exercise
     * is not found, or the set index is invalid, the operation is silently ignored.
     * 
     * @param groupId The unique identifier of the muscle group.
     * @param exerciseId The unique identifier of the exercise.
     * @param setIndex The zero-based index of the set to remove.
     */
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
    
    /**
     * Updates a set entry with new values.
     * 
     * The set data is validated (reps and weight must be between 0 and 1000).
     * If validation fails, the index is invalid, or any required entity is not found,
     * the operation is silently ignored.
     * 
     * @param groupId The unique identifier of the muscle group.
     * @param exerciseId The unique identifier of the exercise.
     * @param setIndex The zero-based index of the set to update.
     * @param reps The number of repetitions (0-1000).
     * @param weight The weight in kilograms (0.0-1000.0).
     * @param comment Optional comment for the set. Can be null or empty.
     */
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
    
    /**
     * Removes a set entry from an exercise by its unique ID.
     * 
     * If no current session exists, the muscle group is not found, the exercise
     * is not found, or the set ID is not found, the operation is silently ignored.
     * 
     * @param groupId The unique identifier of the muscle group.
     * @param exerciseId The unique identifier of the exercise.
     * @param setId The unique identifier of the set to remove.
     */
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
    
    /**
     * Starts the workout timer.
     * 
     * If the timer is already running, this function does nothing.
     * The timer increments every second and updates the timer state.
     */
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
    
    /**
     * Pauses the workout timer.
     * 
     * Stops the timer coroutine and updates the state to indicate the timer
     * is no longer running. The elapsed time is preserved.
     */
    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        
        _timerState.update { currentState ->
            currentState.copy(isRunning = false)
        }
    }
    
    /**
     * Resets the workout timer to zero.
     * 
     * Stops the timer if running and resets the elapsed time to 0 seconds.
     */
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

package com.journal.ernie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.journal.ernie.data.Exercise
import com.journal.ernie.data.MuscleGroup
import com.journal.ernie.data.SetEntry
import com.journal.ernie.data.WorkoutSession
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
    
    // Public read-only state flows
    val currentSession: StateFlow<WorkoutSession?> = _currentSession.asStateFlow()
    val allSessions: StateFlow<List<WorkoutSession>> = _allSessions.asStateFlow()
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    
    init {
        loadSessions()
    }
    
    // Session Management Functions
    
    fun loadSessions() {
        // TODO: Load from Room database when implemented
        // For now, sessions are stored in memory only
        // This function is called in init block
    }
    
    fun createNewSession(name: String) {
        viewModelScope.launch {
            val newSession = WorkoutSession.createNew(name)
            
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
}

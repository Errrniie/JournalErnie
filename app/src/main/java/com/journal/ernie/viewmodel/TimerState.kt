package com.journal.ernie.viewmodel

/**
 * Represents the state of the workout timer.
 * 
 * The timer tracks elapsed time in seconds and whether it is currently running.
 * 
 * @param elapsedTimeSeconds The total elapsed time in seconds (default: 0).
 * @param isRunning Whether the timer is currently running (default: false).
 */
data class TimerState(
    val elapsedTimeSeconds: Long = 0L,
    val isRunning: Boolean = false
) {
    companion object {
        /**
         * Creates an initial timer state with zero elapsed time and stopped.
         * 
         * @return A TimerState with elapsedTimeSeconds = 0 and isRunning = false.
         */
        fun initial(): TimerState {
            return TimerState(
                elapsedTimeSeconds = 0L,
                isRunning = false
            )
        }
    }
}

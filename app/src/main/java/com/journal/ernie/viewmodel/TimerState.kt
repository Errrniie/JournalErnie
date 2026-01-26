package com.journal.ernie.viewmodel

data class TimerState(
    val elapsedTimeSeconds: Long = 0L,
    val isRunning: Boolean = false
) {
    companion object {
        fun initial(): TimerState {
            return TimerState(
                elapsedTimeSeconds = 0L,
                isRunning = false
            )
        }
    }
}

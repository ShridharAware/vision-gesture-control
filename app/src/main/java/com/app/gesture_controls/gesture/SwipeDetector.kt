package com.app.gesture_controls.gesture

import com.app.gesture_controls.actions.Action
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import android.util.Log

class SwipeDetector {

    companion object {
        private const val TAG = "GestureControl"
        private const val HISTORY_SIZE = 5
        private const val SWIPE_THRESHOLD = 0.15f // Minimum movement in normalized coords
        private const val SWIPE_MIN_VELOCITY = 0.02f // Minimum movement per frame
    }

    private val xHistory = mutableListOf<Float>()
    private val yHistory = mutableListOf<Float>()
    private var lastSwipeTime = 0L
    private val swipeCooldown = 1000L // 1 second between swipes

    fun update(landmarks: List<NormalizedLandmark>): Action {
        if (landmarks.size < 21) return Action.NONE

        val wrist = landmarks[0]
        val currentX = wrist.x()
        val currentY = wrist.y()

        xHistory.add(currentX)
        yHistory.add(currentY)
        
        if (xHistory.size > HISTORY_SIZE) {
            xHistory.removeAt(0)
            yHistory.removeAt(0)
        }

        if (yHistory.size < HISTORY_SIZE) return Action.NONE

        val now = System.currentTimeMillis()
        if (now - lastSwipeTime < swipeCooldown) return Action.NONE

        val deltaX = xHistory.last() - xHistory.first()
        val deltaY = yHistory.last() - yHistory.first()
        
        val absX = Math.abs(deltaX)
        val absY = Math.abs(deltaY)

        if (absY > absX && absY > SWIPE_THRESHOLD) {
            val action = if (deltaY < 0) {
                Log.d(TAG, "SWIPE UP DETECTED")
                Action.SCROLL_UP
            } else {
                Log.d(TAG, "SWIPE DOWN DETECTED")
                Action.SCROLL_DOWN
            }
            lastSwipeTime = now
            clearHistory()
            return action
        } else if (absX > absY && absX > SWIPE_THRESHOLD) {
            val action = if (deltaX < 0) {
                Log.d(TAG, "SWIPE LEFT DETECTED")
                Action.VOLUME_DOWN
            } else {
                Log.d(TAG, "SWIPE RIGHT DETECTED")
                Action.VOLUME_UP
            }
            lastSwipeTime = now
            clearHistory()
            return action
        }

        return Action.NONE
    }

    private fun clearHistory() {
        xHistory.clear()
        yHistory.clear()
    }
}

package com.app.gesture_controls.gesture

class DoublePinchDetector {

    private var firstPinchTime = 0L

    private val doublePinchWindow = 1500L

    fun update(
        pinchDetected: Boolean
    ): Boolean {

        if (!pinchDetected) {
            return false
        }

        val now = System.currentTimeMillis()

        if (
            firstPinchTime != 0L &&
            now - firstPinchTime <= doublePinchWindow
        ) {

            firstPinchTime = 0L

            return true
        }

        firstPinchTime = now

        return false
    }
}
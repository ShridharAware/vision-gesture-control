package com.app.gesture_controls.gesture

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.sqrt

class PinchDetector {

    private var pinched = false

    // These are relative to the size of the hand.
    private val pinchRatio = 0.35f
    private val releaseRatio = 0.50f

    fun update(
        landmarks: List<NormalizedLandmark>
    ): Boolean {

        if (landmarks.size != 21) {
            return false
        }

        val wrist = landmarks[0]
        val thumbTip = landmarks[4]
        val indexTip = landmarks[8]
        val middleMcp = landmarks[9]

        val thumbIndexDistance = distance(
            thumbTip.x(),
            thumbTip.y(),
            indexTip.x(),
            indexTip.y()
        )

        val handSize = distance(
            wrist.x(),
            wrist.y(),
            middleMcp.x(),
            middleMcp.y()
        )

        if (handSize <= 0f) {
            return false
        }

        val ratio = thumbIndexDistance / handSize

        println("PINCH RATIO = $ratio")

        // Pinch started
        if (!pinched && ratio < pinchRatio) {

            pinched = true

            return true
        }

        // Pinch released
        if (pinched && ratio > releaseRatio) {

            pinched = false
        }

        return false
    }

    private fun distance(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): Float {

        val dx = x2 - x1
        val dy = y2 - y1

        return sqrt(
            dx * dx + dy * dy
        )
    }
}
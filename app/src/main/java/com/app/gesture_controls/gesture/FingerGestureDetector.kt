package com.app.gesture_controls.gesture

import com.app.gesture_controls.actions.Action
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.sqrt

class FingerGestureDetector {

    private val pinchRatio = 0.35f
    private val releaseRatio = 0.50f
    
    // Tracking pinch state per finger
    private var isIndexPinched = false
    private var isMiddlePinched = false
    private var isRingPinched = false
    private var isPinkyPinched = false

    fun update(landmarks: List<NormalizedLandmark>): Action {
        if (landmarks.size != 21) return Action.NONE

        val wrist = landmarks[0]
        val middleMcp = landmarks[9]
        val thumbTip = landmarks[4]
        
        val handSize = distance(wrist, middleMcp)
        if (handSize <= 0f) return Action.NONE

        // Finger tips: Index(8), Middle(12), Ring(16), Pinky(20)
        val indexTip = landmarks[8]
        val middleTip = landmarks[12]
        val ringTip = landmarks[16]
        val pinkyTip = landmarks[20]

        // 1. Check Index Pinch (TAP)
        val indexAction = updateFingerPinch(thumbTip, indexTip, handSize, isIndexPinched) { isIndexPinched = it }
        if (indexAction) return Action.TAP

        // 2. Check Middle Pinch (SCROLL UP / NEXT)
        val middleAction = updateFingerPinch(thumbTip, middleTip, handSize, isMiddlePinched) { isMiddlePinched = it }
        if (middleAction) return Action.SCROLL_UP

        // 3. Check Ring Pinch (SCROLL DOWN / PREV)
        val ringAction = updateFingerPinch(thumbTip, ringTip, handSize, isRingPinched) { isRingPinched = it }
        if (ringAction) return Action.SCROLL_DOWN

        // 4. Check Pinky Pinch (VOLUME UP)
        val pinkyAction = updateFingerPinch(thumbTip, pinkyTip, handSize, isPinkyPinched) { isPinkyPinched = it }
        if (pinkyAction) return Action.VOLUME_UP

        // 5. Check FIST (VOLUME DOWN)
        if (isFist(landmarks, handSize)) {
            // We use a small internal state to avoid multiple triggers for fist
            return Action.VOLUME_DOWN
        }

        return Action.NONE
    }

    private fun updateFingerPinch(
        thumb: NormalizedLandmark,
        finger: NormalizedLandmark,
        handSize: Float,
        currentlyPinched: Boolean,
        setPinched: (Boolean) -> Unit
    ): Boolean {
        val dist = distance(thumb, finger)
        val ratio = dist / handSize

        if (!currentlyPinched && ratio < pinchRatio) {
            setPinched(true)
            return true
        }
        if (currentlyPinched && ratio > releaseRatio) {
            setPinched(false)
        }
        return false
    }

    private fun isFist(landmarks: List<NormalizedLandmark>, handSize: Float): Boolean {
        // Simple fist detection: all finger tips are close to the wrist
        val wrist = landmarks[0]
        val threshold = 0.6f * handSize
        
        val indexTip = landmarks[8]
        val middleTip = landmarks[12]
        val ringTip = landmarks[16]
        val pinkyTip = landmarks[20]

        return distance(wrist, indexTip) < threshold &&
               distance(wrist, middleTip) < threshold &&
               distance(wrist, ringTip) < threshold &&
               distance(wrist, pinkyTip) < threshold
    }

    private fun distance(l1: NormalizedLandmark, l2: NormalizedLandmark): Float {
        val dx = l1.x() - l2.x()
        val dy = l1.y() - l2.y()
        return sqrt(dx * dx + dy * dy)
    }
}

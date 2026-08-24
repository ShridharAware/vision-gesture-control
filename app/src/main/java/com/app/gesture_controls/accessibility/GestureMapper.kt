package com.app.gesture_controls.accessibility

import android.accessibilityservice.AccessibilityService

object GestureMapper {

    fun fromGestureId(gestureId: Int): Gesture {
        return when (gestureId) {

            AccessibilityService.GESTURE_SWIPE_UP ->
                Gesture.SWIPE_UP

            AccessibilityService.GESTURE_SWIPE_DOWN ->
                Gesture.SWIPE_DOWN

            AccessibilityService.GESTURE_SWIPE_LEFT ->
                Gesture.SWIPE_LEFT

            AccessibilityService.GESTURE_SWIPE_RIGHT ->
                Gesture.SWIPE_RIGHT

            else ->
                Gesture.UNKNOWN
        }
    }
}
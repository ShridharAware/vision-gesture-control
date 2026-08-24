package com.app.gesture_controls.accessibility
import com.app.gesture_controls.actions.Action
import com.app.gesture_controls.actions.ActionExecutor

import android.accessibilityservice.AccessibilityGestureEvent
import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class GestureAccessibilityService : AccessibilityService() {

    private lateinit var actionExecutor: ActionExecutor

    companion object {
        private const val TAG = "GestureControl"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        Log.d(TAG, "Accessibility Service connected")

        serviceInfo = serviceInfo.apply {
            flags = flags or
                    android.accessibilityservice.AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE
        }

        actionExecutor = ActionExecutor(this)

        Log.d(TAG, "Touch exploration mode requested")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We'll use this later to determine the current foreground app.
    }

    override fun onGesture(
        gestureEvent: AccessibilityGestureEvent
    ): Boolean {

        val gesture = GestureMapper.fromGestureId(
            gestureEvent.gestureId
        )

        Log.d(TAG, "GESTURE DETECTED → $gesture")

        when (gesture) {

            Gesture.SWIPE_LEFT -> {
                actionExecutor.execute(Action.BACK)
            }

            Gesture.SWIPE_RIGHT -> {
                actionExecutor.execute(Action.VOLUME_UP)
            }

            Gesture.SWIPE_UP -> {
                actionExecutor.execute(Action.HOME)
            }

            Gesture.SWIPE_DOWN -> {
                actionExecutor.execute(Action.VOLUME_DOWN)
            }

            Gesture.UNKNOWN -> {
                // Nothing
            }
        }

        return true
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility Service interrupted")
    }
}
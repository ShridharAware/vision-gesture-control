package com.app.gesture_controls.actions

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.media.AudioManager
import android.util.Log

class ActionExecutor(
    private val service: AccessibilityService
) {

    companion object {
        private const val TAG = "GestureControl"
    }

    private val audioManager =
        service.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val displayMetrics = service.resources.displayMetrics
    private val screenWidth = displayMetrics.widthPixels
    private val screenHeight = displayMetrics.heightPixels

    fun execute(action: Action) {

        Log.d(TAG, "Executing action → $action")

        when (action) {

            Action.BACK -> {
                service.performGlobalAction(
                    AccessibilityService.GLOBAL_ACTION_BACK
                )
            }

            Action.HOME -> {
                service.performGlobalAction(
                    AccessibilityService.GLOBAL_ACTION_HOME
                )
            }

            Action.RECENTS -> {
                service.performGlobalAction(
                    AccessibilityService.GLOBAL_ACTION_RECENTS
                )
            }

            Action.VOLUME_UP -> {
                audioManager.adjustVolume(
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_SHOW_UI
                )
            }

            Action.VOLUME_DOWN -> {
                audioManager.adjustVolume(
                    AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_SHOW_UI
                )
            }

            Action.SCROLL_UP -> {
                // Swipe from bottom to top (Next Reel)
                swipe(
                    screenWidth / 2f,
                    screenHeight * 0.7f,
                    screenWidth / 2f,
                    screenHeight * 0.3f,
                    500
                )
            }

            Action.SCROLL_DOWN -> {
                // Swipe from top to bottom (Previous Reel)
                swipe(
                    screenWidth / 2f,
                    screenHeight * 0.3f,
                    screenWidth / 2f,
                    screenHeight * 0.7f,
                    500
                )
            }

            Action.TAP -> {
                tap(screenWidth / 2f, screenHeight / 2f)
            }

            Action.NONE -> {
                // Do nothing
            }
        }
    }

    private fun swipe(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        durationMs: Long = 300
    ) {
        val path = Path()
        path.moveTo(startX, startY)
        path.lineTo(endX, endY)

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(
            GestureDescription.StrokeDescription(
                path,
                0,
                durationMs
            )
        )

        service.dispatchGesture(
            gestureBuilder.build(),
            null,
            null
        )
    }

    private fun tap(x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(
            GestureDescription.StrokeDescription(
                path,
                0,
                50
            )
        )

        service.dispatchGesture(
            gestureBuilder.build(),
            null,
            null
        )
    }
}

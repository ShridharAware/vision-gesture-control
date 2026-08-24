package com.app.gesture_controls.actions

import android.accessibilityservice.AccessibilityService
import android.content.Context
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

            Action.NONE -> {
                // Do nothing
            }
        }
    }
}
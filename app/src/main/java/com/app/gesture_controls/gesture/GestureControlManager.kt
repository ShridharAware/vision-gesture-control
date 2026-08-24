package com.app.gesture_controls.gesture

class GestureControlManager {

    var state: GestureControlState =
        GestureControlState.INACTIVE
        private set

    fun toggle() {

        state = when (state) {

            GestureControlState.INACTIVE ->
                GestureControlState.ACTIVE

            GestureControlState.ACTIVE ->
                GestureControlState.INACTIVE
        }
    }

    fun isActive(): Boolean {
        return state == GestureControlState.ACTIVE
    }
}
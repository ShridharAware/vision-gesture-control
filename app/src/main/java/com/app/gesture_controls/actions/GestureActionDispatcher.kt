package com.app.gesture_controls.actions

import android.util.Log

object GestureActionDispatcher {
    private const val TAG = "GestureControl"
    private var actionExecutor: ActionExecutor? = null

    fun setExecutor(executor: ActionExecutor?) {
        actionExecutor = executor
        Log.d(TAG, "ActionExecutor registered with Dispatcher")
    }

    fun dispatch(action: Action) {
        if (action == Action.NONE) return
        
        Log.d(TAG, "Dispatching action: $action")
        val executor = actionExecutor
        if (executor != null) {
            executor.execute(action)
        } else {
            Log.w(TAG, "Cannot dispatch action: ActionExecutor not registered")
        }
    }
}

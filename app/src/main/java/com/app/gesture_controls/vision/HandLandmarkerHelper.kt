package com.app.gesture_controls.vision

import android.content.Context
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandLandmarkerHelper(
    private val context: Context,
    private val onResult: (HandLandmarkerResult) -> Unit
) {

    companion object {
        private const val TAG = "GestureControl"
        private const val MODEL_NAME = "hand_landmarker.task"
    }

    private var handLandmarker: HandLandmarker? = null

    fun setup() {

        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(MODEL_NAME)
            .build()

        val options = HandLandmarker.HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumHands(2)
            .setMinHandDetectionConfidence(0.5f)
            .setMinHandPresenceConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .setResultListener { result, _ ->
                onResult(result)
            }
            .setErrorListener { error ->
                Log.e(TAG, "MediaPipe error", error)
            }
            .build()

        handLandmarker =
            HandLandmarker.createFromOptions(
                context,
                options
            )

        Log.d(TAG, "Hand Landmarker initialized")
    }

    fun detect(
        bitmap: android.graphics.Bitmap,
        timestamp: Long
    ) {

        val mpImage =
            BitmapImageBuilder(bitmap).build()

        handLandmarker?.detectAsync(
            mpImage,
            timestamp
        )
    }

    fun close() {
        handLandmarker?.close()
        handLandmarker = null

        Log.d(TAG, "Hand Landmarker closed")
    }
}
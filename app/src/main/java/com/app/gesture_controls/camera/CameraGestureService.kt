package com.app.gesture_controls.camera

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.LifecycleService
import com.app.gesture_controls.gesture.DoublePinchDetector
import com.app.gesture_controls.gesture.GestureControlState
import com.app.gesture_controls.gesture.PinchDetector
import com.app.gesture_controls.gesture.SwipeDetector
import com.app.gesture_controls.vision.HandLandmarkerHelper
import com.app.gesture_controls.actions.Action
import com.app.gesture_controls.actions.GestureActionDispatcher

class CameraGestureService : LifecycleService() {

    companion object {
        private const val TAG = "GestureControl"
        private const val CHANNEL_ID = "camera_gesture_service_channel"
        private const val NOTIFICATION_ID = 1

        private val _currentState = mutableStateOf(GestureControlState.INACTIVE)
        val currentState: State<GestureControlState> = _currentState

        fun start(context: Context) {
            val intent = Intent(context, CameraGestureService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, CameraGestureService::class.java)
            context.stopService(intent)
        }
    }

    private lateinit var handLandmarkerHelper: HandLandmarkerHelper
    private lateinit var cameraManager: CameraManager
    private val pinchDetector = PinchDetector()
    private val doublePinchDetector = DoublePinchDetector()
    private val swipeDetector = SwipeDetector()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "CameraGestureService created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "CameraGestureService starting")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                createNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
            )
        } else {
            startForeground(
                NOTIFICATION_ID,
                createNotification()
            )
        }

        setupCameraPipeline()
        _currentState.value = GestureControlState.ACTIVE
        Log.d(TAG, "Gesture Control → ACTIVE")

        return START_NOT_STICKY
    }

    private fun setupCameraPipeline() {
        handLandmarkerHelper = HandLandmarkerHelper(this) { result ->
            val hands = result.landmarks()
            if (hands.isNotEmpty()) {
                val primaryHand = hands[0]

                // 1. Detect Pinch / Tap / Double Pinch
                val pinchDetected = pinchDetector.update(primaryHand)
                if (pinchDetected) {
                    Log.d(TAG, "PINCH detected")
                    
                    val doublePinch = doublePinchDetector.update(pinchDetected)
                    if (doublePinch) {
                        Log.d(TAG, "DOUBLE PINCH detected")
                        Log.d(TAG, "Gesture Control → INACTIVE")
                        stopSelf()
                    } else {
                        // If not a double pinch (yet), it might be a tap
                        GestureActionDispatcher.dispatch(Action.TAP)
                    }
                }

                // 2. Detect Swipes (Scroll / Volume)
                val swipeAction = swipeDetector.update(primaryHand)
                if (swipeAction != Action.NONE) {
                    GestureActionDispatcher.dispatch(swipeAction)
                }
            }
        }
        handLandmarkerHelper.setup()

        cameraManager = CameraManager(
            context = this,
            lifecycleOwner = this,
            handLandmarkerHelper = handLandmarkerHelper
        )
        cameraManager.startCamera()
        Log.d(TAG, "Camera started")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "CameraGestureService stopping")
        if (::handLandmarkerHelper.isInitialized) {
            handLandmarkerHelper.close()
        }
        _currentState.value = GestureControlState.INACTIVE
        Log.d(TAG, "Camera released")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Camera Gesture Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Gesture Control Active")
            .setContentText("Camera is observing gestures")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()
    }
}

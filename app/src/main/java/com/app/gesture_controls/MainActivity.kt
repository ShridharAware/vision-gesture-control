package com.app.gesture_controls

import com.app.gesture_controls.camera.CameraManager
import com.app.gesture_controls.vision.HandLandmarkerHelper
import com.app.gesture_controls.vision.HandOverlay

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                showCamera()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else {
            cameraPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    private fun showCamera() {

        setContent {

            val previewView = remember {
                PreviewView(this)
            }

            val handOverlay = remember {
                HandOverlay(this)
            }

            DisposableEffect(Unit) {

                val handLandmarkerHelper =
                    HandLandmarkerHelper(
                        context = this@MainActivity
                    ) { result ->

                        val hands = result.landmarks()

                        if (hands.isNotEmpty()) {

                            handOverlay.setHands(hands)

                        } else {

                            handOverlay.clear()
                        }
                    }

                handLandmarkerHelper.setup()

                val cameraManager =
                    CameraManager(
                        context = this@MainActivity,
                        lifecycleOwner = this@MainActivity,
                        handLandmarkerHelper = handLandmarkerHelper
                    )

                cameraManager.startCamera(previewView)

                onDispose {
                    handLandmarkerHelper.close()
                }
            }

            AndroidView(
                factory = {

                    android.widget.FrameLayout(this).apply {

                        addView(
                            previewView,
                            android.widget.FrameLayout.LayoutParams(
                                -1,
                                -1
                            )
                        )

                        addView(
                            handOverlay,
                            android.widget.FrameLayout.LayoutParams(
                                -1,
                                -1
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
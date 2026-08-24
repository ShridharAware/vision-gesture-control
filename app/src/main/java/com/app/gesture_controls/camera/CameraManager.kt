package com.app.gesture_controls.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.app.gesture_controls.vision.HandLandmarkerHelper

class CameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val handLandmarkerHelper: HandLandmarkerHelper
) {

    fun startCamera(previewView: PreviewView) {

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            val cameraProvider =
                cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()

            preview.surfaceProvider =
                previewView.surfaceProvider

            val imageAnalysis =
                ImageAnalysis.Builder()
                    .setBackpressureStrategy(
                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                    )
                    .build()

            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                CameraAnalyzer(handLandmarkerHelper)
            )

            val cameraSelector =
                CameraSelector.DEFAULT_FRONT_CAMERA

            try {

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

            } catch (exception: Exception) {

                exception.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(context))
    }
}
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

    fun startCamera(previewView: PreviewView? = null) {

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            val cameraProvider =
                cameraProviderFuture.get()

            val preview = if (previewView != null) {
                Preview.Builder()
                    .build()
                    .also {
                        it.surfaceProvider =
                            previewView.surfaceProvider
                    }
            } else {
                null
            }

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

                val useCases = mutableListOf<androidx.camera.core.UseCase>()
                preview?.let { useCases.add(it) }
                useCases.add(imageAnalysis)

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    *useCases.toTypedArray()
                )

            } catch (exception: Exception) {

                exception.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(context))
    }
}

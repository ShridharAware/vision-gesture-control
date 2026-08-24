package com.app.gesture_controls.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.app.gesture_controls.vision.HandLandmarkerHelper

class CameraAnalyzer(
    private val handLandmarkerHelper: HandLandmarkerHelper
) : ImageAnalysis.Analyzer {

    companion object {
        private const val TAG = "GestureControl"
    }

    override fun analyze(imageProxy: ImageProxy) {

        try {

            val bitmap = imageProxy.toBitmap()

            val rotation =
                imageProxy.imageInfo.rotationDegrees

            val rotatedBitmap =
                rotateBitmap(
                    bitmap,
                    rotation
                )

            handLandmarkerHelper.detect(
                rotatedBitmap,
                System.currentTimeMillis()
            )

        } catch (exception: Exception) {

            Log.e(
                TAG,
                "Frame analysis failed",
                exception
            )

        } finally {

            imageProxy.close()
        }
    }

    private fun rotateBitmap(
        bitmap: Bitmap,
        rotationDegrees: Int
    ): Bitmap {

        if (rotationDegrees == 0) {
            return bitmap
        }

        val matrix = Matrix()

        matrix.postRotate(
            rotationDegrees.toFloat()
        )

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}
package com.app.gesture_controls.vision

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class HandOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val pointPaint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = 8f
        isAntiAlias = true
    }

    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }

    private var hands: List<List<NormalizedLandmark>> = emptyList()
    fun setHands(
        newHands: List<List<NormalizedLandmark>>
    ) {
        hands = newHands
        postInvalidate()
    }

    fun clear() {
        hands = emptyList()
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        hands.forEach { landmarks ->

            if (landmarks.size != 21) {
                return@forEach
            }

            HAND_CONNECTIONS.forEach { (start, end) ->

                val startPoint = landmarks[start]
                val endPoint = landmarks[end]

                canvas.drawLine(
                    (1f - startPoint.x()) * width,
                    startPoint.y() * height,
                    (1f - endPoint.x()) * width,
                    endPoint.y() * height,
                    linePaint
                )
            }

            landmarks.forEach { landmark ->

                canvas.drawCircle(
                    (1f - landmark.x()) * width,
                    landmark.y() * height,
                    8f,
                    pointPaint
                )
            }
        }
    }
    companion object {

        private val HAND_CONNECTIONS = listOf(
            0 to 1,
            1 to 2,
            2 to 3,
            3 to 4,

            0 to 5,
            5 to 6,
            6 to 7,
            7 to 8,

            0 to 9,
            9 to 10,
            10 to 11,
            11 to 12,

            0 to 13,
            13 to 14,
            14 to 15,
            15 to 16,

            0 to 17,
            17 to 18,
            18 to 19,
            19 to 20,

            5 to 9,
            9 to 13,
            13 to 17
        )
    }
}
# Gesture Control

A hands-free camera-based gesture controller for Android devices. This app allows users to interact with their phone (e.g., scrolling through Instagram Reels, YouTube Shorts, or adjusting volume) without physically touching the screen, using the front-facing camera to detect hand gestures.

## Features

- **Background Processing**: Uses an Android Foreground Service with a camera type to continue gesture recognition even when the app is minimized.
- **Real-time Detection**: Leverages MediaPipe Hand Landmarker for high-performance, low-latency hand tracking.
- **System Integration**: Uses the Android Accessibility Service to perform system-wide actions like swipes, taps, and volume adjustments.

## Gestures

The app supports two types of gestures: high-precision finger pinches and broader hand movements.

### Finger Pinch Gestures (Recommended)
These gestures require minimal effort and can be performed while keeping the hand steady.

| Gesture | Action |
| :--- | :--- |
| **Thumb + Middle Finger** | **Scroll Up (Next Reel / Swipe Up)** |
| **Thumb + Ring Finger** | **Scroll Down (Previous Reel / Swipe Down)** |
| **Thumb + Index Finger** | **Tap (Single Pinch)** |
| **Thumb + Pinky Finger** | **Volume Up** |
| **Fist (Closed Hand)** | **Volume Down** |

### System Control
| Gesture | Action |
| :--- | :--- |
| **Double Pinch (Index)** | **Deactivate Gesture Control** (Stops camera & service) |

### Hand Movement Gestures (Secondary)
| Movement | Action |
| :--- | :--- |
| **Swipe Hand Up** | Scroll Up |
| **Swipe Hand Down** | Scroll Down |
| **Swipe Hand Right** | Volume Up |
| **Swipe Hand Left** | Volume Down |

## Setup Instructions

1. **Install the App**: Build and run the project on a physical Android device.
2. **Grant Permissions**:
   - **Camera**: Required to observe hand movements.
   - **Notifications**: Required to show the Foreground Service status (Android 13+).
3. **Enable Accessibility Service**:
   - Go to **Settings > Accessibility**.
   - Find **"Gesture Control"** under downloaded services/installed apps.
   - Turn it **ON**. (The app provides a direct link to this setting for convenience).
4. **Start Control**: Open the app and tap **"Start Gesture Control"**. You can now leave the app and use it in other applications.

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Camera**: CameraX (ImageAnalysis)
- **AI/Vision**: MediaPipe Hand Landmarker (`tasks-vision`)
- **Architecture**:
  - `CameraGestureService`: Foreground Service owning the camera and AI pipeline.
  - `GestureAccessibilityService`: Responsible for executing system actions.
  - `FingerGestureDetector` & `SwipeDetector`: Custom logic for landmark-to-action mapping.

## Developer Notes

- The app uses `ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST` to ensure real-time responsiveness.
- Gesture cooldowns are optimized (500ms) to allow rapid scrolling through content.
- Coordinate systems are mirrored to align with the front camera's preview.

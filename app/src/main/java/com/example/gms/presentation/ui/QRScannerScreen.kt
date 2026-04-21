package com.example.gms.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

@Composable
fun QRScannerScreen(onQrCodeScanned: (String) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val hasPermission = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    if (!hasPermission) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera Permission Required", color = Color.White)
        }
        // You should trigger the permission launcher here
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    if (!hasCameraPermission) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text("Camera permission is required", color = Color.White)
        }
        return
    }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Camera Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    val scanner = BarcodeScanning.getClient()

                    imageAnalysis.setAnalyzer(executor) { imageProxy ->
                        processImageProxy(scanner, imageProxy, onQrCodeScanned)
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("Camera", "Binding failed", e)
                    }
                }, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Y Pay Style Overlay
        QRViewfinderOverlay(onBack)
    }
}

@Composable
fun QRViewfinderOverlay(onBack: () -> Unit) {
    // Animation for the "Scanning" line
    val infiniteTransition = rememberInfiniteTransition(label = "scanning")
    val lineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "linePosition"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Semi-transparent background with a hole in the middle
        Canvas(modifier = Modifier.fillMaxSize()) {
            val squareSize = 250.dp.toPx()
            val left = (size.width - squareSize) / 2
            val top = (size.height - squareSize) / 2
            val right = left + squareSize
            val bottom = top + squareSize
            val cornerLength = 40.dp.toPx()
            val strokeWidth = 4.dp.toPx()
            val gPayBlue = Color(0xFF1A73E8)
            // Draw background
            drawRect(color = Color.Black.copy(alpha = 0.6f))

            // Clear the middle square (the viewfinder)
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(squareSize, squareSize),
                blendMode = BlendMode.Clear
            )

            // 3. Draw Blue Corners
            // Top Left
            drawLine(gPayBlue, Offset(left, top), Offset(left + cornerLength, top), strokeWidth)
            drawLine(gPayBlue, Offset(left, top), Offset(left, top + cornerLength), strokeWidth)

            // Top Right
            drawLine(gPayBlue, Offset(right, top), Offset(right - cornerLength, top), strokeWidth)
            drawLine(gPayBlue, Offset(right, top), Offset(right, top + cornerLength), strokeWidth)

            // Bottom Left
            drawLine(
                gPayBlue,
                Offset(left, bottom),
                Offset(left + cornerLength, bottom),
                strokeWidth
            )
            drawLine(
                gPayBlue,
                Offset(left, bottom),
                Offset(left, bottom - cornerLength),
                strokeWidth
            )

            // Bottom Right
            drawLine(
                gPayBlue,
                Offset(right, bottom),
                Offset(right - cornerLength, bottom),
                strokeWidth
            )
            drawLine(
                gPayBlue,
                Offset(right, bottom),
                Offset(right, bottom - cornerLength),
                strokeWidth
            )

            // 4. Moving Laser Line
            val lineY = top + (squareSize * lineOffset)
            drawLine(
                color = gPayBlue.copy(alpha = 0.6f),
                start = Offset(left + 10.dp.toPx(), lineY),
                end = Offset(right - 10.dp.toPx(), lineY),
                strokeWidth = 2.dp.toPx()
            )
        }

        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 40.dp, start = 16.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Text(
            "Align QR code within frame to pay",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@SuppressLint("UnsafeOptInUsageError")
fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onQrCodeScanned: (String) -> Unit
) {
    val inputImage =
        InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
    barcodeScanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            barcodes.firstOrNull()?.rawValue?.let { qrCode ->
                onQrCodeScanned(qrCode)
            }
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}
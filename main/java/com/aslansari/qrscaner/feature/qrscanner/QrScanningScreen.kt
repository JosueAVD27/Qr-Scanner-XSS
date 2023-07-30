@file:OptIn(ExperimentalPermissionsApi::class)

package com.aslansari.qrscaner.feature.qrscanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.aslansari.qrscaner.feature.permission.camera.FeatureThatRequiresCameraPermission
import com.aslansari.qrscaner.feature.permission.camera.NeedCameraPermissionScreen
import com.aslansari.qrscaner.feature.permission.galery.NeedGalleryPermissionScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.withContext
import java.net.URL

typealias AndroidSize = android.util.Size

@Composable
@ExperimentalGetImage
fun QrScanningScreen(
    viewModel: QrScanViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val galleryPermissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    cameraPermissionState.launchPermissionRequest()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    val preview = Preview.Builder().build()
    val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
        .setTargetResolution(
            AndroidSize(previewView.width, previewView.height)
        )
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    val targetRect by remember { derivedStateOf { uiState.targetRect } }

    LaunchedEffect(targetRect) {
        imageAnalysis.setAnalyzer(
            Dispatchers.Default.asExecutor(),
            QrCodeAnalyzer(
                targetRect = targetRect.toAndroidRect(),
                previewView = previewView,
            ) { result ->
                viewModel.onQrCodeDetected(result)
            }
        )
    }

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(uiState.lensFacing)
        .build()
    var camera by remember { mutableStateOf<Camera?>(null) }

    LaunchedEffect(uiState.lensFacing) {
        val cameraProvider = ProcessCameraProvider.getInstance(context)
        camera = withContext(Dispatchers.IO) {
            cameraProvider.get()
        }.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)

        previewView.post {
            preview.setSurfaceProvider(previewView.surfaceProvider)
        }
    }

    FeatureThatRequiresCameraPermission(
        deniedContent = { status ->
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "SEGURIDAD INFORMATICA",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Permiso de Camara
                NeedCameraPermissionScreen(
                    requestPermission = cameraPermissionState::launchPermissionRequest,
                    shouldShowRationale = status.shouldShowRationale
                )

                /*Spacer(modifier = Modifier.height(10.dp))

                // Permiso de Galeria
                NeedGalleryPermissionScreen(
                    requestPermission = galleryPermissionState::launchPermissionRequest,
                    shouldShowRationale = status.shouldShowRationale
                )*/

                Spacer(modifier = Modifier.height(100.dp))
            }
        },
        grantedContent = {
            Scaffold { paddingValues ->
                Content(
                    modifier = Modifier.padding(paddingValues),
                    uiState = uiState,
                    previewView = previewView,
                    onTargetPositioned = viewModel::onTargetPositioned
                )
            }
        }
    )
}


@Composable
private fun Content(
    modifier: Modifier,
    previewView: PreviewView,
    uiState: QrScanUIState,
    onTargetPositioned: (Rect) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 5.dp, end = 5.dp),
            factory = {
                previewView
            }
        )

        // Diseño de la ventana de scanner
        canvaScreen(onTargetPositioned)

        // Elementos de la parte superior de la ventana
        topScreen(uiState)

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elementos no visibles de la parte inferior
            if (uiState.detectedQR.isNotEmpty()) {
                bottomElementsdisable(uiState)
            }

            // Elementos visubles de la ventana inferior
            // NO USADO
            bottomScreen()
        }

        val context = LocalContext.current
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .zIndex(1f)
                .padding(end = 5.dp)
        ) {
            ClickableText(
                text = AnnotatedString("by AJVD"),
                modifier = Modifier.padding(bottom = 5.dp),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.5f)
                ),
                onClick = { openURL(context, "https://armandovelasquez.com") }
            )
        }
    }
}

@Composable
private fun canvaScreen(onTargetPositioned: (Rect) -> Unit){
    val widthInPx: Float
    val heightInPx: Float
    val radiusInPx: Float
    with(LocalDensity.current) {
        widthInPx = 250.dp.toPx()
        heightInPx = 250.dp.toPx()
        radiusInPx = 16.dp.toPx()
    }
    val linePosition = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        linePosition.animateTo(1f, animationSpec = infiniteRepeatable(tween(1500), repeatMode = RepeatMode.Reverse))
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = .5f)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .size(250.dp)
                .border(1.dp, Color.White, RoundedCornerShape(16.dp))
                .onGloballyPositioned {
                    onTargetPositioned(it.boundsInRoot())
                }
        ) {
            val offset = Offset(
                x = (size.width - widthInPx) / 2,
                y = (size.height - heightInPx) / 2,
            )
            val cutoutRect = Rect(offset, Size(widthInPx, heightInPx))
            // Source
            drawRoundRect(
                topLeft = cutoutRect.topLeft,
                size = cutoutRect.size,
                cornerRadius = CornerRadius(radiusInPx, radiusInPx),
                color = Color.Transparent,
                blendMode = BlendMode.Clear
            )

            // Animacion linea roja
            val lineStartY = offset.y + heightInPx * linePosition.value
            val lineEndY = lineStartY
            drawLine(
                color = Color.Red,
                start = Offset(offset.x, lineStartY),
                end = Offset(offset.x + widthInPx, lineEndY),
                strokeWidth = 2f
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier,
                text = "QR Scanner XSS",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = .6f),
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(250.dp))

            Text(
                modifier = Modifier,
                text = "ESPE",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = .6f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun topScreen(uiState: QrScanUIState){
    Box (
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 10.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.5f),
                                Color.White.copy(alpha = 0.2f)
                            ),
                            startX = 0f,
                            endX = Float.POSITIVE_INFINITY
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = "SEGURIDAD INFORMATICA",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )

            Text(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.5f),
                                Color.White.copy(alpha = 0.2f)
                            ),
                            startX = 0f,
                            endX = Float.POSITIVE_INFINITY
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = "ATAQUES XSS",
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(modifier = Modifier
                .padding(start = 30.dp, end = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                if (uiState.detectedQR.isNotEmpty()) {
                    if (isURL(uiState.detectedQR)) {
                        val isVulnerable = vulnerableURL(uiState.detectedQR)
                        if (isVulnerable != null) {
                            Text(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = .2f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                text = buildAnnotatedString {
                                    append("La URL es vulnerable a XSS. Se encuentra ejecutando en la ruta este payload:\n")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Red.run { copy(red = red * 0.8f, green = green * 0.8f, blue = blue * 0.8f) })) {
                                        append("$isVulnerable")
                                    }
                                },
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = .2f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                text = "La URL no es vulnerable a XSS."
                            )
                        }
                    }
                }
            }
        }
    }
}

// Elementos no visibles de la ventana inferior
@Composable
private fun bottomElementsdisable(uiState: QrScanUIState){
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 30.dp, end = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isVulnerable = clasificarURL(uiState.detectedQR)
            if (isURL(uiState.detectedQR)) {
                val resultText = if (isVulnerable) {
                    "Vulnerable"
                } else {
                    "No vulnerable"
                }
                Text(
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .background(Color.White.copy(alpha = .2f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    text = resultText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isVulnerable) Color.Red.run { copy(red = red * 0.8f, green = green * 0.8f, blue = blue * 0.8f) } else Color.Green.run { copy(red = red * 0.8f, green = green * 0.8f, blue = blue * 0.8f) }
                )
            } else {
                // Código para el caso en que no se detecte una URL válida
                Text(
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .background(Color.White.copy(alpha = .2f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    text = "No es una URL válida",
                    color = Color.Red.run { copy(red = red * 0.8f, green = green * 0.8f, blue = blue * 0.8f) },
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .background(Color.White.copy(alpha = .4f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = uiState.detectedQR,
            )

            if (isURL(uiState.detectedQR)) {
                ClickableText(
                    text = AnnotatedString("Ir a enlace"),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .background(
                            if (isVulnerable) Color.Red.copy(alpha = .6f) else Color.Green.copy(
                                alpha = .6f
                            ), RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 80.dp, vertical = 8.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    onClick = { openURL(context, uiState.detectedQR) }
                )
            }
        }
    }
}

// Elementos visibles de la ventana inferior
@Composable
private fun bottomScreen(){
    val galleryPermissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*Button(
                onClick = {
                    if (galleryPermissionState.launchPermissionRequest().equals(false)) {
                        galleryPermissionState.launchPermissionRequest()
                    } else {

                    }
                }
            ) {
                Text(
                    text = "Scan image",
                    modifier = Modifier
                        .padding(horizontal = 100.dp)
                )
            }*/

            Text(
                modifier = Modifier.padding(bottom = 5.dp),
                text = "ITIN",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

/*
* Funciones
*/

// Clasificador de url para ataques XSS
fun clasificarURL(url: String): Boolean {
    val document = org.jsoup.nodes.Document("")
    document.html("<html><body><a href=\"$url\">Test</a></body></html>")
    val anchor = document.select("a")
    val href = anchor.attr("href")
    val img = document.select("img")
    val src = img.attr("src")
    val xssPayloads = listOf(
        "<script>",
        "javascript:",
        "onload=",
        "onerror=",
        "onmouseover=",
        "message=",
        "onmessage=",
        "eval(",
        "expression(",
        "vbscript:",
        "data:",
        "xss:",
        ";alert"
        // Agrega aquí más payloads de XSS comunes que desees detectar
    )
    for (payload in xssPayloads) {
        if (href.contains(payload, ignoreCase = true) || src.contains(payload, ignoreCase = true)) {
            return true
        }
    }
    return false
}

// Función auxiliar para verificar si una cadena es una URL válida
fun isURL(str: String): Boolean {
    return try {
        URL(str)
        true
    } catch (e: Exception) {
        false
    }
}

// Funcion para abrir la ruta
fun openURL(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}


// Funcion para obtener la vulnerabilidad de la url
fun vulnerableURL(url: String): String? {
    val document = org.jsoup.nodes.Document("")
    document.html("<html><body><a href=\"$url\">Test</a></body></html>")
    val anchor = document.select("a")
    val href = anchor.attr("href")
    val img = document.select("img")
    val src = img.attr("src")
    val xssPayloads = listOf(
        "<script>",
        "javascript:",
        "onload=",
        "onerror=",
        "onmouseover=",
        "message=",
        "onmessage=",
        "eval(",
        "expression(",
        "vbscript:",
        "data:",
        "xss:",
        ";alert"
        // Agrega aquí más payloads de XSS comunes que desees detectar
    )
    for (payload in xssPayloads) {
        if (href.contains(payload, ignoreCase = true) || src.contains(payload, ignoreCase = true)) {
            return payload
        }
    }
    return null
}
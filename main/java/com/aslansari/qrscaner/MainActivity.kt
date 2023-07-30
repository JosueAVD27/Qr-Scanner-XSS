package com.aslansari.qrscaner

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ExperimentalGetImage
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.aslansari.qrscaner.feature.qrscanner.QrScanningScreen
import com.aslansari.qrscaner.ui.theme.QrScanerTheme

@ExperimentalGetImage
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val activity = LocalContext.current as Activity
            QrScanerTheme {
                // A surface container using the 'background' color from the theme
                QrScanningScreen(
                    viewModel = hiltViewModel()
                )
            }
        }
    }

}

@Composable
private fun diseño(){
    Box(modifier = Modifier.fillMaxSize()) {

        val widthInPx: Float
        val heightInPx: Float
        val radiusInPx: Float
        val linePosition = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            linePosition.animateTo(1f, animationSpec = infiniteRepeatable(tween(1500), repeatMode = RepeatMode.Reverse))
        }
        with(LocalDensity.current) {
            widthInPx = 250.dp.toPx()
            heightInPx = 250.dp.toPx()
            radiusInPx = 16.dp.toPx()
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
                                Color.White.copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.3f),
                                Color.White.copy(alpha = 0.1f)
                            ),
                            startX = 0f,
                            endX = Float.POSITIVE_INFINITY
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 26.dp, vertical = 8.dp),
                text = "SEGURIDAD INFORMATICA",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )

            Text(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.3f),
                                Color.White.copy(alpha = 0.1f)
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
                Text(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = .2f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    text = buildAnnotatedString {
                        append("La URL es vulnerable a XSS. Se encuentra ejecutando en la ruta este payload:\n")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Red.run { copy(red = red * 0.8f, green = green * 0.8f, blue = blue * 0.8f) })) {
                            append("<script>")
                        }
                    },
                    textAlign = TextAlign.Center
                )
            }

        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elementos no visibles de la parte inferior
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 30.dp, end = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 15.dp)
                            .background(Color.White.copy(alpha = .2f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        text = "No vulnerable",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

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

                    Text(
                        modifier = Modifier
                            .padding(bottom = 15.dp)
                            .background(Color.White.copy(alpha = .4f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        text = "https://armandovelasquez.comaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    )

                    ClickableText(
                        text = AnnotatedString("Ir a enlace"),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .background(
                                Color.Green.copy(alpha = .6f), RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 80.dp, vertical = 8.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        onClick = {  }
                    )

                }
            }

            // Elementos visubles de la ventana inferior
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                onClick = {  }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun proview(){
    diseño()
}
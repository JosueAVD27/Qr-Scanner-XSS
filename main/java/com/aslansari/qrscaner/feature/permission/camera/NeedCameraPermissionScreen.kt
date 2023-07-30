package com.aslansari.qrscaner.feature.permission.camera

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun NeedCameraPermissionScreen(
    requestPermission: () -> Unit,
    shouldShowRationale: Boolean,
) {
    val textToShow = if (shouldShowRationale) {
        "La cámara es importante para esta aplicación. Por favor, conceda el permiso."
    } else {
        "Permiso de cámara necesario para que esta función esté disponible. " +
                "Por favor, conceda el permiso"
    }

    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = textToShow,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = requestPermission
    ) {
        Text(
            text = "Solicitar permiso",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
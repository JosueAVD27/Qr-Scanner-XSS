package com.aslansari.qrscaner.feature.permission.galery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun NeedGalleryPermissionScreen(
    requestPermission: () -> Unit,
    shouldShowRationale: Boolean,
) {
    val textToShow = if (shouldShowRationale) {
        "El acceso a la galería es necesario para esta aplicación. Por favor, concede el permiso."
    } else {
        "Permiso de acceso a la galería necesario para que esta función esté disponible. " +
                "Por favor, concede el permiso."
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

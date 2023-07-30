@file:OptIn(ExperimentalAnimationApi::class)

package com.aslansari.qrscaner.feature.permission.galery

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FeatureThatRequiresGalleryPermission(
    deniedContent: @Composable (PermissionStatus.Denied) -> Unit,
    grantedContent: @Composable () -> Unit
) {
    // Gallery permission state
    val galleryPermissionState = rememberPermissionState(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val status = galleryPermissionState.status
    AnimatedContent(targetState = status) { permissionStatus ->
        when(permissionStatus) {
            is PermissionStatus.Granted -> grantedContent()
            is PermissionStatus.Denied -> deniedContent(permissionStatus)
        }
    }
}
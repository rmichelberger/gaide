package games.thinkin.camera

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kashif.cameraK.controller.CameraController
import com.kashif.cameraK.enums.CameraLens
import com.kashif.cameraK.enums.Directory
import com.kashif.cameraK.enums.FlashMode
import com.kashif.cameraK.enums.ImageFormat
import com.kashif.cameraK.enums.QualityPrioritization
import com.kashif.cameraK.enums.TorchMode
import com.kashif.cameraK.permissions.Permissions
import com.kashif.cameraK.permissions.providePermissions
import com.kashif.cameraK.result.ImageCaptureResult
import com.kashif.cameraK.ui.CameraPreview
import gaide.composeapp.generated.resources.Res
import gaide.composeapp.generated.resources.capture_image_button_text
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun CameraView(onImageCaptured: (ByteArray) -> Unit) {
    val permissions: Permissions = providePermissions()
    val coroutineScope = rememberCoroutineScope()

    Scaffold {
        val cameraPermissionState = remember { mutableStateOf(permissions.hasCameraPermission()) }

        val cameraController = remember { mutableStateOf<CameraController?>(null) }

        PermissionsHandler(
            permissions = permissions,
            cameraPermissionState = cameraPermissionState,
        )

        if (cameraPermissionState.value) {
            CameraContent(
                cameraController = cameraController,
            )
            cameraController.value?.let { controller ->
                Box(
                    modifier = Modifier.fillMaxSize().clickable(
                        onClick = {
                            coroutineScope.launch {
                                handleImageCapture(
                                    cameraController = controller,
                                    onImageCaptured = onImageCaptured
                                )
                            }
                        },
                    ), contentAlignment = Alignment.Center
                ) { Text(stringResource(Res.string.capture_image_button_text)) }
            }
        }
    }
}

@Composable
private fun PermissionsHandler(
    permissions: Permissions,
    cameraPermissionState: MutableState<Boolean>,
) {
    if (!cameraPermissionState.value) {
        permissions.RequestCameraPermission(
            onGranted = { cameraPermissionState.value = true },
            onDenied = { println("Camera Permission Denied") }
        )
    }
}

@Composable
private fun CameraContent(
    cameraController: MutableState<CameraController?>,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            cameraConfiguration = {
                setCameraLens(CameraLens.BACK)
                setFlashMode(FlashMode.AUTO)
                setImageFormat(ImageFormat.JPEG)
                setDirectory(Directory.PICTURES)
                setTorchMode(TorchMode.AUTO)
                setQualityPrioritization(QualityPrioritization.QUALITY)
            },
            onCameraControllerReady = {
                cameraController.value = it
            }
        )
    }
}

private suspend fun handleImageCapture(
    cameraController: CameraController,
    onImageCaptured: (ByteArray) -> Unit
) {
    when (val result = cameraController.takePicture()) {
        is ImageCaptureResult.Success -> {
            onImageCaptured(result.byteArray)
        }

        is ImageCaptureResult.Error -> {
            println("Image Capture Error: ${result.exception.message}")
        }
    }
}

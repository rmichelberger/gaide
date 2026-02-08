package games.thinkin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import games.thinkin.AppViewModel.State.Camera
import games.thinkin.AppViewModel.State.Picture
import games.thinkin.camera.CameraView
import games.thinkin.chat.ChatView
import games.thinkin.chat.ChatViewViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val viewModel = viewModel { AppViewModel() }
    when (val state = viewModel.state.collectAsState().value) {
        is Camera -> CameraView(onImageCaptured = viewModel::onImageCaptured)
        is Picture -> ChatView(
            byteArray = state.byteArray,
            viewModel = viewModel {
                ChatViewViewModel(
                    geminiApi = viewModel.geminiApi,
                )
            },
            onBack = viewModel::openCamera
        )
    }
}

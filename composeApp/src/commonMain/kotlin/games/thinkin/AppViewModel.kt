package games.thinkin

import androidx.lifecycle.ViewModel
import games.thinkin.gemini.api.GeminiApi
import games.thinkin.network.Network
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    val geminiApi = GeminiApi(network = Network())

    private val _state = MutableStateFlow<State>(value = State.Camera)
    val state = _state.asStateFlow()

    /*
    init {
        viewModelScope.launch {

            val byteArray = Res.readBytes("files/deo_small.jpeg")
//            val byteArray = Res.readBytes("files/deo.jpeg")
//            val base64Img = Base64Encoder.encode(byteArray)
            _state.value = State.Picture(byteArray = byteArray)
        }
    }*/

    fun onImageCaptured(byteArray: ByteArray) {
        _state.value = State.Picture(byteArray = byteArray)
    }

    fun openCamera() {
        _state.value = State.Camera
    }

    sealed class State {
        object Camera : State()
        data class Picture(val byteArray: ByteArray) : State() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as Picture

                if (!byteArray.contentEquals(other.byteArray)) return false

                return true
            }

            override fun hashCode(): Int {
                return byteArray.contentHashCode()
            }
        }
    }
}
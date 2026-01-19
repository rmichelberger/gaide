package games.thinkin.camera.encoding

import android.util.Base64

actual object Base64Encoder {
    actual fun encode(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}

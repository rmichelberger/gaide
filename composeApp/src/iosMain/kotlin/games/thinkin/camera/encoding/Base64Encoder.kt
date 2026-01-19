package games.thinkin.camera.encoding

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.base64Encoding

@OptIn(ExperimentalForeignApi::class)
actual object Base64Encoder {
    actual fun encode(bytes: ByteArray): String {
        val data = bytes.toNSData()
        return data.base64Encoding()
    }
}

@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toNSData(): NSData {
    return NSData()
//    val pinned = this.pin()
//    return NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
}

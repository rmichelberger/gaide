package games.thinkin.camera.encoding

expect object Base64Encoder {
    fun encode(bytes: ByteArray): String
}

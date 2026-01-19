package games.thinkin.gemini.api

import games.thinkin.gemini.file.GeminiFileUploader
import games.thinkin.network.Network
import games.thinkin.network.Url
import io.ktor.client.call.body

class GeminiApi(
    private val network: Network,
    private val geminiFileUploader: GeminiFileUploader = GeminiFileUploader(network = network)
) {
    suspend fun uploadFile(byteArray: ByteArray) =
        geminiFileUploader.uploadFile(byteArray = byteArray, apiKey = "")

    suspend fun generateContent(
        request: Request,
        model: Model = Model.Gemini3FlashPreview,
        apiKey: String
    ): Response = network.post(
        url = Url(
            baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/${model.path}:generateContent",
            headers = mapOf("x-goog-api-key" to apiKey)
        ), body = request
    ).body()

    sealed class Model(val path: String) {
        object Gemini3FlashPreview : Model(path = "gemini-3-flash-preview")
    }
}
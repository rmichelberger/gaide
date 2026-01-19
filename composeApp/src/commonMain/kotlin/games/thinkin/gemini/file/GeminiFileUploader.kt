package games.thinkin.gemini.file

import games.thinkin.network.Network
import games.thinkin.network.Url
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class GeminiFileUploader(private val network: Network) {

    private suspend fun getFileUploadUrl(
        contentLength: Int,
        mimeType: String = "image/jpeg",
        displayName: String = "IMAGE",
        apiKey: String
    ): String? = withContext(Dispatchers.IO) {
        val response = network.post(
            url = Url(
                baseUrl = "https://generativelanguage.googleapis.com/upload/v1beta/files",
                headers = mapOf(
                    "x-goog-api-key" to apiKey,
                    "X-Goog-Upload-Protocol" to "resumable",
                    "X-Goog-Upload-Command" to "start",
                    "X-Goog-Upload-Header-Content-Length" to contentLength.toString(),
                    "X-Goog-Upload-Header-Content-Type" to mimeType
                )
            ), body = "{'file':{'display_name':'$displayName'}}"
        )
        response.headers["x-goog-upload-url"]
    }

    suspend fun uploadFile(
        byteArray: ByteArray,
        apiKey: String
    ): FileUploadResponse? = withContext(Dispatchers.IO) {
        val contentLength = byteArray.size
        getFileUploadUrl(contentLength = contentLength, apiKey = apiKey)?.let { url ->
            network.post(
                url = Url(
                    baseUrl = url,
                    headers = mapOf(
                        "x-goog-api-key" to apiKey,
                        "Content-Length" to contentLength.toString(),
                        "X-Goog-Upload-Offset" to "0",
                        "X-Goog-Upload-Command" to "upload, finalize",
                    )
                ), body = byteArray
            ).body()
        }
    }
}
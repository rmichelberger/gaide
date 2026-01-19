package games.thinkin.gemini.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileData(
    @SerialName(value = "mime_type") val mimeType: String,
    @SerialName(value = "file_uri") val fileUri: String
)
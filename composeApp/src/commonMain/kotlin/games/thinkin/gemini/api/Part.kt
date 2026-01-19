package games.thinkin.gemini.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Part(
    val text: String? = null,
    @SerialName(value = "inline_data") val inlineData: InlineData? = null,
    @SerialName(value = "file_data") val fileData: FileData? = null
)
package games.thinkin.gemini.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InlineData(@SerialName(value = "mime_type") val mimeType: String, val data: String)
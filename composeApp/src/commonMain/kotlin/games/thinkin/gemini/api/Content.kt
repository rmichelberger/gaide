package games.thinkin.gemini.api

import kotlinx.serialization.Serializable

@Serializable
data class Content(val role: String? = null, val parts: List<Part>? = null)
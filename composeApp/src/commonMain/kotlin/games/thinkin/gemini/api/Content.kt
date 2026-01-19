package games.thinkin.gemini.api

import kotlinx.serialization.Serializable

@Serializable
data class Content(val role: String, val parts: List<Part>)
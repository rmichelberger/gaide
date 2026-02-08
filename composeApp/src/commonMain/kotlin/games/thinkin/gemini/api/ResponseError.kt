package games.thinkin.gemini.api

import kotlinx.serialization.Serializable

@Serializable
data class ResponseError(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)
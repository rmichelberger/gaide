package games.thinkin.gemini.api

import kotlinx.serialization.Serializable

@Serializable
data class Candidate(val content: Content)
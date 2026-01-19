package games.thinkin.gemini.api

import kotlinx.serialization.Serializable

@Serializable
data class Response(val candidates: List<Candidate>)
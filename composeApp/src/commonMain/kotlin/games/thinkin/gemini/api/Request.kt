package games.thinkin.gemini.api

import kotlinx.serialization.Serializable

@Serializable
data class Request(val contents: List<Content>, val generationConfig: GenerationConfig? = null)
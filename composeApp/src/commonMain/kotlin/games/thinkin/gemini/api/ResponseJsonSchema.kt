package games.thinkin.gemini.api

import kotlinx.serialization.Serializable

@Serializable
data class ResponseJsonSchema(
    val type: String,
    val properties: Properties,
    val required: List<String>
)

@Serializable
data class Properties(
    val answer: Property,
    val questions: Property
)

@Serializable
data class Property(val type: String, val items: Item? = null)

@Serializable
data class Item(val type: String)
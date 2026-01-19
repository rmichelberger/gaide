package games.thinkin.gemini.file

import kotlinx.serialization.Serializable

@Serializable
data class File(val name: String, val uri: String, val mimeType: String, val displayName: String)
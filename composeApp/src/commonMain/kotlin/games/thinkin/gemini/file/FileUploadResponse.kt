package games.thinkin.gemini.file

import kotlinx.serialization.Serializable

@Serializable
data class FileUploadResponse(val file: File)
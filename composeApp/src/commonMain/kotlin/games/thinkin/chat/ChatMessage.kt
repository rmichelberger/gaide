package games.thinkin.chat

import games.thinkin.gemini.api.Content

data class ChatMessage(val messageType: MessageType, val texts: List<String>) {
    constructor(content: Content) : this(
        messageType = content.messageType,
        texts = content.parts.firstNotNullOf { part ->
            part.text?.lines()?.filter { line -> line.isNotEmpty() }
        }
    )
}

//        val responseContents = response.candidates.map { it.content }
//        val parts = responseContents.flatMap { it.parts }


private val Content.messageType: MessageType
    get() = if (this.role == "user") MessageType.USER else MessageType.MODEL

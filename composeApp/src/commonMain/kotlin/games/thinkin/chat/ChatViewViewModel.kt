package games.thinkin.chat

import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gaide.composeapp.generated.resources.Res
import gaide.composeapp.generated.resources.loading_answer_text
import gaide.composeapp.generated.resources.uploading_image_text
import games.thinkin.fullLanguageName
import games.thinkin.gemini.GeminiSession
import games.thinkin.gemini.api.Content
import games.thinkin.gemini.api.FileData
import games.thinkin.gemini.api.GeminiApi
import games.thinkin.gemini.api.GenerationConfig
import games.thinkin.gemini.api.Part
import games.thinkin.gemini.api.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import secrets.Secrets

class ChatViewViewModel(private val geminiApi: GeminiApi, byteArray: ByteArray) :
    ViewModel() {

    private val _state =
        MutableStateFlow<State>(value = State.Loading(textResource = Res.string.loading_answer_text))
    val state = _state.asStateFlow()

    private var geminiSession = GeminiSession(contents = emptyList())

    init {
        viewModelScope.launch {
            getPictureInfo(byteArray = byteArray)
        }
    }

//    init {
//        val dummyMessages = DummyMessages()
//        _state.value = State.Chat(messages = dummyMessages.messages)
//    }

    fun getPictureInfo(byteArray: ByteArray) {
        // clear session history
        geminiSession = GeminiSession(contents = emptyList())

        _state.value = State.Loading(textResource = Res.string.uploading_image_text)
        viewModelScope.launch {
            // upload image
            val fileResponse = uploadFile(byteArray = byteArray)
            fileResponse?.file?.uri?.let { uri ->
                // get picture info
                getPictureInfo(uri = uri)
            }
//            getPictureInfo(uri = "https://generativelanguage.googleapis.com/v1beta/files/cv1n37cu2w3z")
        }
    }

    private suspend fun uploadFile(byteArray: ByteArray) =
        geminiApi.uploadFile(byteArray = byteArray)


    private val generationConfig: GenerationConfig?
        get() = null
    /*        get() {
                val properties = Properties(
                    answer = Property(type = "string"),
                    questions = Property(type = "array", items = Item(type = "string"))
                )
                val responseJsonSchema = ResponseJsonSchema(
                    type = "object",
                    properties = properties,
                    required = listOf("answer", "questions")
                )
                return GenerationConfig(
                    responseMimeType = "application/json",
                    responseJsonSchema = responseJsonSchema
                )
            }*/

    private fun getPictureInfo(uri: String) {
        val contents = listOf(
            Content(
                role = "model",
                parts = listOf(
                    Part(
                        text = "You are a guide to a visually impaired person. No intro, only the content. Text must be accessible, it will be read out loud, so don't use any text formatting. You need to explain everything detailed in a way that a blind person can understand it.\nGoal: help to do the shopping.\nTasks:\n1) analyze the picture\n2) explain what you can see on the picture\n3) ask follow up questions related to the content of the picture to help to identify the next step. Keep it short, maximal 1 minute to read. All answers must be in ${Locale.current.fullLanguageName}"
                    )
                )
            ),
            Content(
                role = "user",
                parts = listOf(
                    Part(
                        text = "Explain what you can see on this picture. Be detailed about everything. Use locations, e.g. * on the top left corner\n* in the center\n* on the bottom right corner\n\n.",
                    ),
                    Part(fileData = FileData(mimeType = "image/jpeg", fileUri = uri))
//                         {"file_data":{"mime_type": "'"${MIME1_TYPE}"'", "file_uri": '$file1_uri'}},
//                        Part(
//                            inlineData = InlineData(
//                                mimeType = "image/jpeg",
//                                data = base64
//                            )
//                        )

                )
            )
        )
        sendRequest(requestContents = contents)
    }


//    private suspend fun uploadPicture(base64: String) {
//        geminiApi.uploadFile(base64 = base64)
//    }

//    private suspend fun getPictureInfo(base64: String) {
//        val properties = Properties(
//            answer = Property(type = "string"),
//            questions = Property(type = "array", items = Item(type = "string"))
//        )
//        val responseJsonSchema = ResponseJsonSchema(
//            type = "object",
//            properties = properties,
//            required = listOf("answer", "questions")
//        )
//        val generationConfig = GenerationConfig(
//            responseMimeType = "application/json",
//            responseJsonSchema = responseJsonSchema
//        )
//        val request = Request(
//            generationConfig = generationConfig,
//            contents = listOf(
//                Content(
//                    role = "model",
//                    parts = listOf(
//                        Part(
//                            text = "You are a guide to a visually impaired person. No intro, only the content. Text must be accessible, it will be read out loud, so don't use any text formatting. You need to explain everything detailed in a way that a blind person can understand it.\nGoal: help to do the shopping.\nTasks:\n1) analyze the picture\n2) explain what you can see on the picture\n3) ask follow up questions related to the content of the picture to help to identify the next step"
//                        )
//                    )
//                ),
//                Content(
//                    role = "user",
//                    parts = listOf(
//                        Part(
//                            text = "Explain what you can see on this picture. Be detailed about everything. Use locations, e.g. * on the top left corner\n* in the center\n* on the bottom right corner\n\n.",
//                        ),
//                        Part(
//                            inlineData = InlineData(
//                                mimeType = "image/jpeg",
//                                data = base64
//                            )
//                        )
//
//                    )
//                )
//            )
//        )
//
//        val response = geminiApi.generateContent(request = request)
//        val contents = response.candidates.map { it.content }
//        val parts = contents.flatMap { it.parts }
//        val jsonText = parts.mapNotNull { it.text }
//        println(jsonText)
//        val jsonResponse = Json.decodeFromString<JsonResponse>(jsonText.first())
//        _state.value =
//            State.Chat(answer = jsonResponse.answer, questions = jsonResponse.questions)
//
//    }

    private fun sendRequest(requestContents: List<Content>) {
        _state.value = State.Loading(textResource = Res.string.loading_answer_text)
        viewModelScope.launch {
            val contents = geminiSession.contents + requestContents
            val request = Request(
                generationConfig = generationConfig,
                contents = contents
            )

            val response = geminiApi.generateContent(request = request, apiKey = Secrets.API_KEY)
            val responseContents = response.candidates.map { it.content }
            geminiSession =
                GeminiSession(contents = geminiSession.contents + requestContents + responseContents)
            // remove the first 2 content (system prompt and picture analysis prompt)
            val visibleContents = geminiSession.contents.drop(2)
            _state.value =
                State.Chat(messages = visibleContents.map { ChatMessage(content = it) })
        }
    }

    fun onUserInput(input: String) {
        sendRequest(
            requestContents = listOf(
                Content(
                    role = "user",
                    parts = listOf(Part(text = input))
                )
            )
        )
    }

    sealed class State {
        data class Loading(val textResource: StringResource) : State()
        data class Chat(val messages: List<ChatMessage>) : State()
    }
}
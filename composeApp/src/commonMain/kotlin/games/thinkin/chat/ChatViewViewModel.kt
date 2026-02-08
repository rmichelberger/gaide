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
        }
    }

    private suspend fun uploadFile(byteArray: ByteArray) =
        geminiApi.uploadFile(byteArray = byteArray)

    private fun getPictureInfo(uri: String) {
        val contents = listOf(
            Content(
                role = "user",
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
                )
            )
        )
        sendRequest(requestContents = contents)
    }

    private fun sendRequest(requestContents: List<Content>) {
        _state.value = State.Loading(textResource = Res.string.loading_answer_text)
        viewModelScope.launch {
            try {

                val contents = geminiSession.contents + requestContents
                val request = Request(contents = contents)

                val response =
                    geminiApi.generateContent(request = request, apiKey = Secrets.API_KEY)
                val responseContents =
                    response.candidates?.let { candidates -> candidates.map { it.content } }
                        ?: response.error?.message?.let { message ->
                            listOf(
                                Content(
                                    role = "model",
                                    parts = listOf(Part(text = message))
                                )
                            )
                        }

                responseContents?.let {
                    geminiSession =
                        GeminiSession(contents = geminiSession.contents + requestContents + it)
                }
            } catch (t: Throwable) {
                geminiSession =
                    GeminiSession(
                        contents = geminiSession.contents + requestContents + Content(
                            role = "model", parts = listOf(
                                Part(text = "Error: ${t.message ?: t::class.simpleName}")
                            )
                        )
                    )
            } finally {
                // remove the first 2 content (system prompt and picture analysis prompt)
                val visibleContents = geminiSession.contents.drop(2)
                _state.value =
                    State.Chat(messages = visibleContents.map { ChatMessage(content = it) })
            }
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
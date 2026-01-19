package games.thinkin.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gaide.composeapp.generated.resources.Res
import gaide.composeapp.generated.resources.back
import games.thinkin.CenteredBox
import games.thinkin.chat.ChatViewViewModel.State.*
import games.thinkin.gemini.api.GeminiApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatView(byteArray: ByteArray, geminiApi: GeminiApi, onBack: () -> Unit) {
    val viewModel = viewModel {
        ChatViewViewModel(geminiApi = geminiApi)
    }
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(key1 = byteArray) {
//        viewModel.getPictureInfo(byteArray = byteArray)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(Res.drawable.back),
                            contentDescription = "back to camera"
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (state) {
            is Loading -> LoadingView(modifier = Modifier.padding(innerPadding), text = state.text)
            is Chat -> ChatContent(
                modifier = Modifier.padding(innerPadding),
                messages = state.messages,
                onUserInput = viewModel::onUserInput
            )
        }
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier, text: String) {
    CenteredBox(modifier = modifier.fillMaxSize()) { Text(text = text) }
}

@Composable
private fun ChatContent(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    onUserInput: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.weight(weight = 1f, fill = false).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            items(items = messages) {
                ChatMessageView(message = it)
            }
        }
        InputView(onTextValueChange = onUserInput)
    }
}

@Composable
private fun InputView(
    onTextValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
) {
    val state = rememberTextFieldState()
    val send = {
        onTextValueChange(state.text.toString())
        state.clearText()
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        TextField(
            state = state,
            modifier = Modifier.onKeyEvent {
                if (it.key == Key.Enter) {
                    send()
                    true
                }
                false
            }.weight(weight = 1f, fill = false),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = keyboardType,
            ),
        )
        Button(onClick = send) {
            Text(text = "Send")
        }
    }
}

@Composable
private fun ChatMessageView(message: ChatMessage) {
    when (message.messageType) {
        MessageType.USER ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.width(width = 16.dp))
                TextView(
                    modifier = Modifier.fillMaxWidth(),
                    texts = message.texts,
                    textAlign = TextAlign.End
                )
            }

        MessageType.MODEL ->
            Row(modifier = Modifier.fillMaxWidth()) {
                TextView(
                    modifier = Modifier.fillMaxWidth(),
                    texts = message.texts,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.width(width = 16.dp))
            }
    }
}

@Composable
private fun TextView(modifier: Modifier = Modifier, texts: List<String>, textAlign: TextAlign) {
    Column {
        repeat(texts.size) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = texts[it],
                textAlign = textAlign
            )
        }
    }
}
package games.thinkin

import androidx.compose.ui.text.intl.Locale

val Locale.fullLanguageName: String
    get() = when (language) {
        "hu" -> "Hungarian"
        "de" -> "German"
        else -> "English"
    }
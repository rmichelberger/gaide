package games.thinkin.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class Network {
    private val client = HttpClient {
        install(plugin = Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        install(plugin = ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = false
                isLenient = true
            })
        }
    }

    suspend fun get(url: Url): HttpResponse = client.get(urlString = url.baseUrl) {
        url {
            url.headers?.let {
                it.forEach { (key, value) -> headers.append(name = key, value = value) }
            }
            url.paths?.let {
                appendPathSegments(segments = it)
            }
            url.queries?.let {
                it.forEach { query ->
                    parameters.append(name = query.first, value = query.second)
                }
            }
        }
    }

    suspend fun post(url: Url, body: Any?): HttpResponse =
        client.post(urlString = url.baseUrl) {
            url.headers?.let {
                it.forEach { (key, value) -> headers.append(name = key, value = value) }
            }
            contentType(type = ContentType.Application.Json)
            setBody(body = body)
        }
}

data class Url(
    val baseUrl: String,
    val paths: List<String>? = null,
    val queries: List<Pair<String, String>>? = null,
    val headers: Map<String, String>? = null
)

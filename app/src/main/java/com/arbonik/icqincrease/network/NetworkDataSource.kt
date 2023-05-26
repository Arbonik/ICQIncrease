package com.arbonik.icqincrease.network

import com.mpmep.classes.GameStatus
import com.mpmep.classes.WSServerResponse
import com.mpmep.plugins.core.ExampleResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

object NetworkDataSource {
    private val ktorClient : HttpClient by lazy {
        HttpClient(CIO) {
            install(Logging){
                level = LogLevel.ALL
            }
            install(WebSockets){
                pingInterval = 20_000
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }

    private val BASE_URL = "http://192.168.1.12:8080"
    suspend fun createRoom() = ktorClient.post("$BASE_URL/rooms")
            .body<RoomRespond>()
    suspend fun allRoom() = ktorClient.get("$BASE_URL/rooms")
        .body<List<RoomRespond>>()

    suspend fun connectToGame(
        id: String,
        output : SharedFlow<ExampleResponse>,
        input : MutableSharedFlow<WSServerResponse>
    ){
        ktorClient.webSocket(method = HttpMethod.Get, host = "192.168.1.12", port = 8080, path = "/rooms/$id") {
            val messageOutputRoutine = launch {
                output.onEach { example ->
                    outgoing.send(Frame.Text(
                        Json.encodeToString(example)
                    ))
                }.launchIn(this)
            }
            val userInputRoutine = launch {
                for (frame in incoming){
                    val response = Json.decodeFromString<WSServerResponse>((frame as Frame.Text).readText())
                    input.emit(response)
                }
            }
            userInputRoutine.join()
            messageOutputRoutine.cancelAndJoin()
        }
    }
}

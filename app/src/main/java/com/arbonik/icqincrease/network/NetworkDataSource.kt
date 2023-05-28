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
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.invoke
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.HttpMethod
import io.ktor.http.parameters
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

    private val BASE_URL = "http://176.57.220.203:8080"
    suspend fun createRoom() = ktorClient.post("$BASE_URL/rooms")
            .body<RoomRespond>()
    suspend fun allRoom() = ktorClient.get("$BASE_URL/rooms")
        .body<List<RoomRespond>>()

    suspend fun connectToGame(
        id: String,
        gender: String,
        age: Int,
        output : SharedFlow<ExampleResponse>,
        input : MutableSharedFlow<WSServerResponse>
    ){
        try {
            ktorClient.webSocket(
                method = HttpMethod.Get,
                host = "176.57.220.203",
                port = 8080,
                path = "/rooms/$id?gender=$gender&age=$age",
            ) {
                val messageOutputRoutine = launch {
                    output.onEach { example ->
                        outgoing.send(
                            Frame.Text(
                                Json.encodeToString(example)
                            )
                        )
                    }.launchIn(this)
                }
                val userInputRoutine = launch {
                    for (frame in incoming) {
                        val response =
                            Json.decodeFromString<WSServerResponse>((frame as Frame.Text).readText())
                        input.emit(response)
                    }
                }
                userInputRoutine.join()
                messageOutputRoutine.cancelAndJoin()
            }
        } catch (e : Throwable){
            input.emit(WSServerResponse(GameStatus.TIMEOUT))
        }
    }
}

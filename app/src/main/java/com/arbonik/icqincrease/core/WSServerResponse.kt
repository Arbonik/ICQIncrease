package com.mpmep.classes

import com.arbonik.icqincrease.core.ExampleState
import com.arbonik.icqincrease.core.Operate
import kotlinx.serialization.Serializable

@Serializable
data class WSServerResponse(
    val gameStatus: GameStatus = GameStatus.EMPTY,
    val example: ExampleState = ExampleState.Example(0, 0, Operate.MINUS)
)
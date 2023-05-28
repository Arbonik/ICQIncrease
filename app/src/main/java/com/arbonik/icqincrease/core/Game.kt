package com.arbonik.icqincrease.core

import com.mpmep.plugins.core.ExampleState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class Game(
    val examples : List<ExampleState.Example>,
    val coroutineScope: CoroutineScope
){

    private val userAnswers : MutableList<Boolean> = mutableListOf()

    private val _currentExample : MutableStateFlow<Int> = MutableStateFlow(0)

    val currentExample: StateFlow<ExampleState> = _currentExample.map {
        examples.getOrElse(it) { ExampleState.ExampleEnd }
    }.stateIn(coroutineScope, SharingStarted.Lazily, examples.first())

    val userMisstake : MutableSharedFlow<String> = MutableSharedFlow()
    fun checkAnswer(answer: Int): Boolean {
        val result = examples.getOrNull(_currentExample.value)?.result() == answer
        if (result) {
            _currentExample.value++
        } else {
            coroutineScope.launch {
                userMisstake.emit("Неверно")
            }
        }
        userAnswers.add(result)
        return result
    }

    fun skip(){
        userAnswers.add(false)
        _currentExample.value++
    }
}

fun generateExample(level : Int = 1): ExampleState.Example {
    val negativeN = -(level * 10 / 2)
    val positiveN = (level * 10 / 2)
    val range = negativeN..positiveN
    val second = range.random()
    val operate = if (second != 0)
        Operate.values().random()
    else
        listOf(Operate.MINUS, Operate.PLUS, Operate.MULTI).random()

    val first = if (operate == Operate.DEV) {
        val n = range.random()
        if (n % second == 0) n
        else
            second * n
    } else range.random()
    return ExampleState.Example(first, second, operate).apply {
        difficulty = level
    }
}

package com.arbonik.icqincrease.presentation.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arbonik.icqincrease.core.Game
import com.arbonik.icqincrease.core.generateExample

class Game1ViewModel: ViewModel() {

    val adapter: Game1Adapter

    private val game = Game(
        List(100){
            generateExample(it)
        },
        viewModelScope
    )

    val nextStateFlow = game.currentExample

    init {
        adapter = Game1Adapter(game.examples)
        Integer.MAX_VALUE
    }


    fun checkResult(answer: Int){
        game.checkAnswer(answer)
    }

    fun skip(){
        game.skip()
    }

}
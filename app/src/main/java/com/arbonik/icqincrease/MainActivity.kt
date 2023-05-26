package com.arbonik.icqincrease

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.mpmep.plugins.core.Game
import com.mpmep.plugins.core.generateExample
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class MainActivity : AppCompatActivity() {
    private val game = Game(
        List(10){
            generateExample(it)
        },
        lifecycleScope
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        game.currentExample.onEach {
            findViewById<TextView>(R.id.textOutput).text = it.toString()
        }.launchIn(lifecycleScope)

        game.userMisstake.onEach {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }.launchIn(lifecycleScope)

        findViewById<Button>(R.id.button).setOnClickListener {
            game.checkAnswer(8)
        }
    }
}
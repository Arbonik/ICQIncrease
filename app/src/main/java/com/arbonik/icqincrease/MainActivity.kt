package com.arbonik.icqincrease

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.mpmep.classes.GameStatus
import com.mpmep.plugins.core.Game
import com.mpmep.plugins.core.generateExample
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class MainActivity : AppCompatActivity() {
//    private val game = Game(
//        List(10){
//            generateExample(it)
//        },
//        lifecycleScope
//    )

    private val gameViewModel : OnlineGameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameViewModel.connectToRoom()

        gameViewModel.sharedFlowIn.onEach { serverResponse ->
            when (serverResponse.gameStatus){
                GameStatus.READY -> {

                }
                GameStatus.FALSE -> {

                }
                GameStatus.GOT_NEW_EXAMPLE -> {
                    findViewById<TextView>(R.id.textOutput).text = "противник решил еще одну!"
                }
                GameStatus.FINISH -> {
                    findViewById<TextView>(R.id.textOutput).text = "ждем пока закончит другой игрок"

                }
                GameStatus.WIN -> {
                    findViewById<TextView>(R.id.textOutput).text = "вы проиграли"
                }
                GameStatus.LOSE -> {
                    findViewById<TextView>(R.id.textOutput).text = "вы победили"
                }
                GameStatus.SHUTDOWN -> {

                }
                GameStatus.AWAIT -> {
                    findViewById<TextView>(R.id.textOutput).text = "Ждем подключение игрока"
                }
                GameStatus.EMPTY -> findViewById<TextView>(R.id.textOutput).text = serverResponse.example.toString()
            }
        }.launchIn(lifecycleScope)

//        game.currentExample.onEach {
//            findViewById<TextView>(R.id.textOutput).text = it.toString()
//        }.launchIn(lifecycleScope)

//        game.userMisstake.onEach {
//            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//        }.launchIn(lifecycleScope)

        findViewById<Button>(R.id.button).setOnClickListener {
            gameViewModel.sendAnswer(2)
        }
    }
}
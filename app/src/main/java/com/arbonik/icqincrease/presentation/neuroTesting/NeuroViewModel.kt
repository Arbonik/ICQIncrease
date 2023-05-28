package com.arbonik.icqincrease.presentation.neuroTesting

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arbonik.icqincrease.core.Game
import com.arbonik.icqincrease.core.Operate
import com.arbonik.icqincrease.core.generateExample
import com.arbonik.icqincrease.ml.Model
import com.arbonik.icqincrease.network.Global
import com.arbonik.icqincrease.presentation.RegistrationFragment.Companion.MAN
import com.mpmep.plugins.core.ExampleState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class NeuroViewModel(application: Application) : AndroidViewModel(
    application
) {
    val game = Game(
        List(10){
            generateExample(it/3 + 1)
        },
        viewModelScope
    )

    val model : Model by lazy {
        Model.newInstance(application)
    }
    fun answer(answer : Int) = game.checkAnswer(answer)

    fun skip(){
        game.skip()
    }

    val currentExample : StateFlow<ExampleState> = game.currentExample

    private val _currentState : MutableStateFlow<List<Int>> = MutableStateFlow(listOf())

    val currentState : StateFlow<Int> = _currentState.map {
        (it.sum() / (it.size + 1))
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    fun predict(exampleState: ExampleState.Example, lastTime : Long) {
        val deltaTime = System.currentTimeMillis() - lastTime
        if (deltaTime < 10_000) {
            val lt = deltaTime.toFloat() / 10_000f
            val gender = if (Global.gender == MAN) 1.0f else 0f
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 7), DataType.FLOAT32)
            val dataToModel = floatArrayOf(
                gender,
                lt,
                exampleState.difficulty.toFloat() / 6.toFloat(),
            ) + opToArray(exampleState.op)

            inputFeature0.loadArray(
                dataToModel
            )
            Log.d("DATA IN MODEL", dataToModel.joinToString())
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            Log.d("DATA IN MODEL", outputFeature0.floatArray.joinToString())

            _currentState.value += (outputFeature0.floatArray.first() * 500).toInt()
            Log.d("DATA IN MODEL", _currentState.value.toString())

        }
    }

    fun opToArray(op : Operate):FloatArray{
        return when (op){
            Operate.PLUS -> floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f)
            Operate.MINUS -> floatArrayOf(0.0f, 1.0f, 0.0f, 0.0f)
            Operate.MULTI -> floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f)
            Operate.DEV -> floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
        }
    }

    override fun onCleared() {
        super.onCleared()
        model.close()
    }
}
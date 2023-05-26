package com.arbonik.icqincrease

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arbonik.icqincrease.network.NetworkDataSource
import com.mpmep.classes.WSServerResponse
import com.mpmep.plugins.core.ExampleResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class OnlineGameViewModel : ViewModel() {

    fun sendAnswer(
        answer : Int
    ){
        viewModelScope.launch {
            sharedFlowOut.emit(ExampleResponse(answer = answer))
        }
    }
    fun skipAnswer(){
        viewModelScope.launch {
            sharedFlowOut.emit(ExampleResponse(isSkip = true))
        }
    }

    private val sharedFlowOut : MutableSharedFlow<ExampleResponse> = MutableSharedFlow()

    private val _sharedFlowIn : MutableSharedFlow<WSServerResponse> = MutableSharedFlow()
    val sharedFlowIn : SharedFlow<WSServerResponse> = _sharedFlowIn
    fun connectToRoom(){
        viewModelScope.launch {
            val id = NetworkDataSource.createRoom().id

            NetworkDataSource.connectToGame(
                id,
                sharedFlowOut,
                _sharedFlowIn
            )
        }
    }
}
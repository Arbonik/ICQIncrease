package com.arbonik.icqincrease.presentation

import androidx.lifecycle.ViewModel
import com.arbonik.icqincrease.network.Global

class RegistrationViewModel: ViewModel() {

    fun saveData(age: Int, gender: String){
        Global.age = age
        Global.gender = gender
    }
}
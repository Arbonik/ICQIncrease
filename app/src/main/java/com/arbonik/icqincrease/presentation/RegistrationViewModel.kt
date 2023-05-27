package com.arbonik.icqincrease.presentation

import androidx.lifecycle.ViewModel
import com.arbonik.icqincrease.network.Global

class RegistrationViewModel: ViewModel() {

    fun saveData(age: Int, gender: Boolean){
        val dataGender = if(gender) "Women" else "Man"
        Global.age = age
        Global.gender = dataGender
    }
}
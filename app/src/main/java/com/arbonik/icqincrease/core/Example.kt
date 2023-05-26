package com.arbonik.icqincrease.core

import kotlinx.serialization.Serializable

@Serializable
sealed class ExampleState {

    @Serializable
    object ExampleEnd : ExampleState()

    @Serializable
    data class Example(
        val first : Int,
        val second:Int,
        val op : Operate
    ) : ExampleState() {
        fun result():Int {
            return when (op){
                Operate.PLUS -> first + second
                Operate.MINUS -> first - second
                Operate.MULTI -> first * second
                Operate.DEV -> first / second
            }
        }

        override fun toString(): String {
            return "$first ${op.s} $second"
        }
    }
}
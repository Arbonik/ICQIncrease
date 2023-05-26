package com.arbonik.icqincrease.core

import kotlinx.serialization.Serializable

@Serializable
enum class Operate(val s : String) {
    PLUS("+"),
    MINUS("-"),
    MULTI("*"),
    DEV("/")
}
package com.queatz.austinhumans.model

data class PersonModel (
        var name: String = "",
        val me: Boolean = false
)

data class MessageModel (
        var message: String = "",
        val me: Boolean = false
)


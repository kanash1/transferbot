package io.transferbot.core.application.dto.command.argument

import io.transferbot.core.application.exception.ObjectBuildException

data class RegisterArgsDto(val chatName: String) {

    class Builder {
        private var _chatName: String? = null

        fun chatName(chatName: String) = apply { _chatName = chatName }
        fun chatName() = _chatName

        fun build() =  try {
            RegisterArgsDto(_chatName!!)
        } catch (e: NullPointerException) {
            throw ObjectBuildException("Not all required fields are filled in")
        }
    }
}
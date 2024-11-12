package io.transferbot.core.application.dto.command.argument

import io.transferbot.core.application.dto.ChatType
import io.transferbot.core.application.exception.ObjectBuildException

data class SendArgsDto(
    val platform: String,
    val chatType: ChatType,
    val chatName: String?,
    val text: String
) {

    class Builder {
        private var _platform: String? = null
        private var _chatType: ChatType? = null
        private var _chatName: String? = null
        private var _text: String = ""

        fun platform() = _platform
        fun chatType() = _chatType
        fun chatName() = _chatName
        fun text() = _text

        fun platform(platform: String) = apply { this._platform = platform }
        fun chatType(chatType: ChatType) = apply { this._chatType = chatType }
        fun chatName(chatName: String?) = apply { this._chatName = chatName }
        fun text(text: String) = apply { this._text = text }

        fun build() = try {
            SendArgsDto(_platform!!, _chatType!!, _chatName, _text)
        } catch (e: NullPointerException) {
            throw ObjectBuildException("Not all required fields are filled in")
        }
    }
}
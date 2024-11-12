package io.transferbot.shared.http.bot.output

import io.transferbot.core.application.dto.ChatType
import io.transferbot.core.application.dto.PostToPlatformDto
import io.transferbot.core.application.port.output.IPostToPlatformOutputPort

class CommandHandlerPresenter(
    private val chatId: String,
    private val postToPlatformPresenter: IPostToPlatformOutputPort
) {
    private fun postTextMessage(message: String) {
        postToPlatformPresenter.postToPlatform(PostToPlatformDto(chatId, message))
    }

    fun failureParse() {
        postTextMessage("Invalid arguments")
    }

    fun failureChatType(chatType: ChatType) {
        postTextMessage("Cannot use in ${chatType.name.lowercase()} chat")
    }

    fun failureApi() {
        postTextMessage("Problem with platform")
    }
}
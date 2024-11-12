package io.transferbot.vk.http.bot.output

import io.transferbot.core.application.dto.ChatType
import io.transferbot.vk.http.bot.model.VkCallbackMessageModel
import io.transferbot.vk.http.bot.model.VkPostMessageModel

interface IVkService {

    companion object {
        const val GROUP_CHAT_PEER_ID_LOWER_BOUND = 2000000000

        fun getChatType(peerId: Long) =
            if (peerId > GROUP_CHAT_PEER_ID_LOWER_BOUND) ChatType.GROUP else ChatType.USER
    }

    fun findMessageByIdInUserChat(id: Long): VkCallbackMessageModel
    fun findMessageByIdInGroupChat(peerId: Long, conversationMessageId: Int): VkCallbackMessageModel
    fun postMessage(vkPostMessageModel: VkPostMessageModel)
    fun getCallbackConfirmationCode(): String
}
package io.transferbot.vk.http.bot.model

import io.transferbot.core.application.dto.AttachmentDto
import io.transferbot.core.application.dto.ChatType

data class VkCallbackMessageModel(
    val fromId: Long,
    val id: Long,
    val isCropped: Boolean,
    val peerId: Long,
    val text: String,
    val attachments: List<AttachmentDto>,
    val chatType: ChatType,
    val conversationMessageId: Int?
)
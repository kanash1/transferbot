package io.transferbot.telegram.http.bot.model

import io.transferbot.core.application.dto.AttachmentType
import io.transferbot.core.application.dto.ChatType

data class TelegramMessageModel(
    val id: Long,
    val fromId: Long,
    val chatId: Long,
    val chatType: ChatType,
    val text: String,
    val attachment: TelegramAttachmentModel? = null
)

data class TelegramAttachmentModel(
    val id: Int,
    val type: AttachmentType,
    val mediaGroupId: Long? = null,
    val name: String? = null
)

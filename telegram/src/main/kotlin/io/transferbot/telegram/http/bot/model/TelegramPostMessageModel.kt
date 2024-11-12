package io.transferbot.telegram.http.bot.model

import io.transferbot.core.application.dto.AttachmentDto

data class TelegramPostMessageModel(
    val chatId: Long,
    val text: String = "",
    val attachments: List<AttachmentDto> = emptyList()
)
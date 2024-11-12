package io.transferbot.vk.http.bot.model

import io.transferbot.core.application.dto.AttachmentDto

data class VkPostMessageModel(
    val peerId: Long,
    val text: String = "",
    val attachments: List<AttachmentDto> = emptyList(),
)
package io.transferbot.core.application.dto

data class PostToPlatformDto(
    val chatId: String,
    val text: String = "",
    val attachments: List<AttachmentDto> = emptyList(),
)

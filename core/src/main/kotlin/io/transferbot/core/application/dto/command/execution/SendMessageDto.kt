package io.transferbot.core.application.dto.command.execution

import io.transferbot.core.application.dto.AttachmentDto
import io.transferbot.core.application.dto.ChatType

data class SendMessageDto(
    val senderPlatformUserId: String,
    val text: String,
    val attachments: List<AttachmentDto>,
    val receiverPlatform: String,
    val receiverChatType: ChatType,
    val receiverChatName: String?
)
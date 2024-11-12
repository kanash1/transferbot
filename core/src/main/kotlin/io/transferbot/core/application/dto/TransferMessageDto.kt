package io.transferbot.core.application.dto

data class TransferMessageDto(
    val userTransferId: Long,
    val text: String,
    val attachments: List<AttachmentDto>,
    val chatType: ChatType,
    val chatName: String?
)
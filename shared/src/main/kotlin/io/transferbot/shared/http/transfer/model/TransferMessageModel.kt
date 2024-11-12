package io.transferbot.shared.http.transfer.model

import io.transferbot.core.application.dto.TransferMessageDto

data class TransferMessageRequestModel(
    val userTransferId: Long,
    val text: String,
    val attachments: List<UrlAttachmentModel>,
    val chatType: String,
    val chatName: String?,
)

fun TransferMessageRequestModel.toDto() = TransferMessageDto(
    userTransferId,
    text,
    attachments.map { it.toDto() },
    enumValueOf(chatType.uppercase()),
    chatName
)

fun TransferMessageDto.toModel() = TransferMessageRequestModel(
    userTransferId,
    text,
    attachments.map { it.toModel() },
    chatType.name.lowercase(),
    chatName
)
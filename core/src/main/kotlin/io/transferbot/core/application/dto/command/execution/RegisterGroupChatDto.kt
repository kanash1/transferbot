package io.transferbot.core.application.dto.command.execution

data class RegisterGroupChatDto(
    val platformUserId: String,
    val groupChatName: String,
    val platformGroupChatId: String
)
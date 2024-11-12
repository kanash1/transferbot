package io.transferbot.core.application.dto.command.execution

data class BindUserDto(
    val platformUserId: String,
    val transferName: String,
    val verificationToken: String
)
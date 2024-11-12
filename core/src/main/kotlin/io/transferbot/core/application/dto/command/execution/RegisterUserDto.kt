package io.transferbot.core.application.dto.command.execution

data class RegisterUserDto(
    val platformUserId: String,
    val username: String
)
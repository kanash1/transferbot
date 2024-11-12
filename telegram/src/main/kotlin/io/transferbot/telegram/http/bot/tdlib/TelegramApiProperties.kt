package io.transferbot.telegram.http.bot.tdlib

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties("telegram-api")
data class TelegramApiProperties(
    @field:NotNull val appId: Int?,
    @field:NotEmpty val appHash: String,
    @field:NotEmpty val botToken: String,
    @field:NotEmpty val sessionPath: String
)

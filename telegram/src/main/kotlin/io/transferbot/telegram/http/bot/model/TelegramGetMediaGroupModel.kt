package io.transferbot.telegram.http.bot.model

data class TelegramGetMediaGroupModel(
    val chatId: Long,
    val messageId: Long,
    val mediaGroupId: Long
)

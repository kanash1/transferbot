package io.transferbot.telegram.http.bot.output

import io.transferbot.telegram.http.bot.model.TelegramGetMediaGroupModel
import io.transferbot.telegram.http.bot.model.TelegramPostMessageModel
import io.transferbot.telegram.http.bot.model.TelegramMessageModel
import java.nio.file.Path

interface ITelegramService {
    suspend fun sendMessage(tgSendMessageRequestDto: TelegramPostMessageModel)
    suspend fun getMediaGroup(tgGetMediaGroupRequestDto: TelegramGetMediaGroupModel): List<TelegramMessageModel>
    suspend fun downloadFile(fileId: Int): Path
}
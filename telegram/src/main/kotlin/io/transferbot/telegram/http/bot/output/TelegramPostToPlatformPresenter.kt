package io.transferbot.telegram.http.bot.output

import io.transferbot.core.application.dto.PostToPlatformDto
import io.transferbot.core.application.port.output.IPostToPlatformOutputPort
import io.transferbot.telegram.http.bot.model.TelegramPostMessageModel
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class TelegramPostToPlatformPresenter(
    private val telegramService: ITelegramService
) : IPostToPlatformOutputPort {

    override fun postToPlatform(postToPlatformDto: PostToPlatformDto) {
        if (postToPlatformDto.text.isNotEmpty() || postToPlatformDto.attachments.isNotEmpty()) {
            runBlocking {
                telegramService.sendMessage(postToPlatformDto.run {
                    TelegramPostMessageModel(
                        chatId.toLong(),
                        text,
                        attachments
                    )
                })
            }
        }
    }
}
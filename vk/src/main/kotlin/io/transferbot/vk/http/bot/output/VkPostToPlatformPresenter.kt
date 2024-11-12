package io.transferbot.vk.http.bot.output

import io.transferbot.core.application.dto.PostToPlatformDto
import io.transferbot.core.application.port.output.IPostToPlatformOutputPort
import io.transferbot.vk.http.bot.model.VkPostMessageModel
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
@Qualifier("vkPostToPlatform")
class VkPostToPlatformPresenter(
    private val vkService: IVkService
): IPostToPlatformOutputPort {

    override fun postToPlatform(postToPlatformDto: PostToPlatformDto) {
        if (postToPlatformDto.text.isNotEmpty() || postToPlatformDto.attachments.isNotEmpty()) {
            vkService.postMessage(postToPlatformDto.run { VkPostMessageModel(chatId.toLong(), text, attachments) })
        }
    }
}
package io.transferbot.vk.http

import com.vk.api.sdk.objects.messages.Message
import com.vk.api.sdk.objects.messages.MessageAttachmentType
import io.transferbot.core.application.dto.AttachmentDto
import io.transferbot.core.application.dto.AttachmentType
import io.transferbot.shared.exception.UnsupportedMediaException
import io.transferbot.vk.http.bot.model.VkCallbackMessageModel
import io.transferbot.vk.http.bot.output.IVkService

fun Message.toModel() = VkCallbackMessageModel(
    fromId,
    id.toLong(),
    isCropped ?: false,
    peerId,
    text ?: "",
    attachments
        ?.filter {
            it.type == MessageAttachmentType.PHOTO || it.type == MessageAttachmentType.DOC
        }?.map {
            when (it.type) {
                MessageAttachmentType.PHOTO -> AttachmentDto(
                    AttachmentType.PHOTO,
                    it.photo.sizes.maxBy { size -> size.type }.url.toString()
                )

                MessageAttachmentType.DOC -> AttachmentDto(
                    AttachmentType.FILE,
                    it.doc.url.toString(),
                    null,
                    it.doc.title
                )

                else -> throw UnsupportedMediaException("Bot not supported ${it.type}")
            }
        } ?: emptyList(),
    IVkService.getChatType(peerId),
    conversationMessageId
)
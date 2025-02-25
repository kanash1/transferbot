package io.transferbot.telegram.http.bot.tdlib

import io.transferbot.core.application.dto.AttachmentDto
import io.transferbot.core.application.dto.AttachmentType
import io.transferbot.core.application.dto.ChatType
import io.transferbot.shared.exception.UnsupportedChatTypeException
import io.transferbot.shared.exception.UnsupportedMediaException
import io.transferbot.telegram.http.bot.model.TelegramAttachmentModel
import io.transferbot.telegram.http.bot.model.TelegramMessageModel
import it.tdlight.jni.TdApi
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.io.path.createTempFile

fun TdApi.Message.toDto(): TelegramMessageModel {
    var text = ""
    var attachment: TelegramAttachmentModel? = null

    val (fromId, chatType) = when (val senderId = senderId) {
        is TdApi.MessageSenderUser -> senderId.userId to ChatType.USER
        is TdApi.MessageSenderChat -> senderId.chatId to ChatType.GROUP
        else -> throw UnsupportedChatTypeException("Unsupported chat type ${senderId::class}")
    }

    when (val content = content) {
        is TdApi.MessageText -> text = content.text.text
        is TdApi.MessagePhoto -> {
            text = content.caption?.text ?: ""
            attachment = content.photo.sizes.maxBy { it.photo.size }.photo.run {
                TelegramAttachmentModel(
                    id,
                    AttachmentType.PHOTO,
                    mediaAlbumId
                )
            }
        }

        is TdApi.MessageDocument -> {
            text = content.caption?.text ?: ""
            attachment = content.document.run {
                TelegramAttachmentModel(
                    document.id,
                    AttachmentType.FILE,
                    mediaAlbumId,
                    fileName
                )
            }
        }

        else -> throw UnsupportedMediaException("Unsupported chat type ${content::class}")
    }

    return TelegramMessageModel(
        id shr 20,
        fromId,
        chatId,
        chatType,
        text,
        attachment
    )
}

fun AttachmentDto.toInputMediaContent(caption: String? = null): TdApi.InputMessageContent {
    val formattedText: TdApi.FormattedText? = caption?.let { TdApi.FormattedText(it, null) }

    when (type) {
        AttachmentType.PHOTO -> {
            val filePath = createTempFile(suffix = ".jpg")
            val tmpFile = filePath.toFile()
            tmpFile.writeBytes(file!!)
            val bimg: BufferedImage = ImageIO.read(tmpFile)
            return TdApi.InputMessagePhoto(
                TdApi.InputFileLocal(tmpFile.absolutePath),
                null,
                IntArray(0),
                bimg.width,
                bimg.height,
                formattedText,
                null,
                false
            )
        }

        AttachmentType.FILE -> {
            val docNameLen = name?.length ?: 0
            val lastDotIdx = name?.lastIndexOf('.') ?: docNameLen
            val tmpFilePath = createTempFile(suffix = name?.substring(lastDotIdx, docNameLen))
            val tmpFile = tmpFilePath.toFile()
            tmpFile.writeBytes(file!!)
            return TdApi.InputMessageDocument(
                TdApi.InputFileLocal(tmpFile.absolutePath),
                null,
                true,
                formattedText
            )
        }
    }
}



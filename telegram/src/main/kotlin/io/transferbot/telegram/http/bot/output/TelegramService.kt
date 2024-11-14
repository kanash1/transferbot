package io.transferbot.telegram.http.bot.output

import io.github.oshai.kotlinlogging.KotlinLogging
import io.transferbot.core.application.dto.AttachmentDto
import io.transferbot.telegram.exception.TelegramApiException
import io.transferbot.telegram.http.bot.model.TelegramGetMediaGroupModel
import io.transferbot.telegram.http.bot.model.TelegramMessageModel
import io.transferbot.telegram.http.bot.model.TelegramPostMessageModel
import io.transferbot.telegram.http.bot.tdlib.TdLib
import io.transferbot.telegram.http.bot.tdlib.toDto
import io.transferbot.telegram.http.bot.tdlib.toInputMediaContent
import it.tdlight.client.TelegramError
import it.tdlight.jni.TdApi
import kotlinx.coroutines.future.await
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.math.max

@Service
class TelegramService(
    @Lazy
    private val tdLib: TdLib
) : ITelegramService {
    private val log = KotlinLogging.logger { }

    override suspend fun sendMessage(tgSendMessageRequestDto: TelegramPostMessageModel) {
        tgSendMessageRequestDto.run {
            when (attachments.size) {
                0 -> sendTextMessage(text, chatId)
                1 -> sendFileMessage(text, attachments[0], chatId)
                else -> sendMessageAlbum(text, attachments, chatId)
            }
        }
    }

    private suspend fun sendTextMessage(text: String, chatId: Long) {
        try {
            val inputContent = TdApi.InputMessageText(TdApi.FormattedText(text, null), null, true)
            tdLib.client.send(TdApi.SendMessage(chatId, 0, null, null, null, inputContent)).await()
        } catch (e: TelegramError) {
            log.error { e.message }
            throw TelegramApiException(e.message, e)
        }
    }

    private suspend fun sendFileMessage(caption: String, attachment: AttachmentDto, chatId: Long) {
        try {
            val inputContent = attachment.toInputMediaContent(caption)
            tdLib.client.send(TdApi.SendMessage(chatId, 0, null, null, null, inputContent)).await()
        } catch (e: TelegramError) {
            log.error { e.message }
            throw TelegramApiException(e.message, e)
        }
    }

    private suspend fun sendMessageAlbum(caption: String, attachments: List<AttachmentDto>, chatId: Long) {
        val inputContents: List<TdApi.InputMessageContent> = attachments.mapIndexed { idx, attachment ->
            if (idx == 0) attachment.toInputMediaContent(caption)
            else attachment.toInputMediaContent()
        }
        tdLib.client.send(TdApi.SendMessageAlbum(chatId, 0, null, null, inputContents.toTypedArray()))
    }

    override suspend fun getMediaGroup(tgGetMediaGroupRequestDto: TelegramGetMediaGroupModel): List<TelegramMessageModel> {
        try {
            val messageId = tgGetMediaGroupRequestDto.messageId
            val messageIds = (max(0, messageId - 9)..<(messageId + 10)).map { it shl 20 }.toList().toLongArray()
            val messages =
                tdLib.client.send(TdApi.GetMessages(tgGetMediaGroupRequestDto.chatId, messageIds)).await().messages
            return messages.filter { it != null && it.mediaAlbumId == tgGetMediaGroupRequestDto.mediaGroupId }
                .map { it.toDto() }
        } catch (e: TelegramError) {
            log.error { e.message }
            throw TelegramApiException(e.message, e)
        }
    }

    override suspend fun downloadFile(fileId: Int): Path {
        try {
            val file = tdLib.client.send(TdApi.DownloadFile(fileId, 1, 0, 0, true)).await()
            return Paths.get(file.local.path)
        } catch (e: TelegramError) {
            log.error { e.message }
            throw TelegramApiException(e.message, e)
        }
    }
}
package io.transferbot.telegram.http.bot.input.handler

import io.transferbot.core.application.dto.AttachmentDto
import io.transferbot.core.application.dto.command.argument.SendArgsDto
import io.transferbot.core.application.dto.command.execution.SendMessageDto
import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.shared.http.bot.input.AbstractSendCommandHandler
import io.transferbot.core.application.service.command.parser.ICommandArgsExtractor
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter
import io.transferbot.telegram.exception.TelegramApiException
import io.transferbot.telegram.http.bot.model.TelegramAttachmentModel
import io.transferbot.telegram.http.bot.model.TelegramGetMediaGroupModel
import io.transferbot.telegram.http.bot.model.TelegramMessageModel
import io.transferbot.telegram.http.bot.output.ITelegramService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.io.path.deleteIfExists
import kotlin.io.path.name
import kotlin.io.path.readBytes

class TelegramSendCommandHandler(
    private val telegramService: ITelegramService,
    private val commandHandlerPresenter: CommandHandlerPresenter,
    botOperationUseCase: IBotOperationsInputPort,
    argsExtractor: ICommandArgsExtractor<SendArgsDto>
): AbstractSendCommandHandler<TelegramMessageModel>(botOperationUseCase, commandHandlerPresenter, argsExtractor) {

    override fun mapToSendData(sendArgs: SendArgsDto, message: TelegramMessageModel): SendMessageDto {
        var attachments = emptyList<AttachmentDto>()

        if (message.attachment != null) {
            attachments = runBlocking {
                if (message.attachment.mediaGroupId == null || message.attachment.mediaGroupId == 0L)
                    listOf(getAttachment(message.attachment))
                else
                    message.run { getAttachments(id, chatId, attachment?.mediaGroupId!!) }
            }
        }

        return SendMessageDto(
            message.fromId.toString(),
            sendArgs.text,
            attachments,
            sendArgs.platform,
            sendArgs.chatType,
            sendArgs.chatName
        )
    }

    private suspend fun getAttachment(tgAttachmentDto: TelegramAttachmentModel): AttachmentDto {
        val path = telegramService.downloadFile(tgAttachmentDto.id)
        val attachment = AttachmentDto(tgAttachmentDto.type, null, path.readBytes(), path.name)
        path.deleteIfExists()
        return attachment
    }

    private suspend fun getAttachments(messageId: Long, chatId: Long, mediaGroupId: Long): List<AttachmentDto> =
        coroutineScope {
            val messages = telegramService.getMediaGroup(TelegramGetMediaGroupModel(chatId, messageId, mediaGroupId))
            messages.map { async { getAttachment(it.attachment!!) } }.awaitAll()
        }

    override fun handle(args: String, message: TelegramMessageModel) {
        try {
            super.handle(args, message)
        } catch (e: TelegramApiException) {
            commandHandlerPresenter.failureApi()
        }
    }
}
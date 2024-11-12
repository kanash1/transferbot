package io.transferbot.vk.http.bot.input.handler

import io.transferbot.core.application.dto.ChatType
import io.transferbot.core.application.dto.command.argument.SendArgsDto
import io.transferbot.core.application.dto.command.execution.SendMessageDto
import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.shared.http.bot.input.AbstractSendCommandHandler
import io.transferbot.core.application.service.command.parser.ICommandArgsExtractor
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter
import io.transferbot.vk.exception.VkApiException
import io.transferbot.vk.http.bot.model.VkCallbackMessageModel
import io.transferbot.vk.http.bot.output.IVkService

class VkSendCommandHandler(
    private val vkService: IVkService,
    private val commandHandlerPresenter: CommandHandlerPresenter,
    botOperationUseCase: IBotOperationsInputPort,
    argsExtractor: ICommandArgsExtractor<SendArgsDto>
): AbstractSendCommandHandler<VkCallbackMessageModel>(botOperationUseCase, commandHandlerPresenter, argsExtractor) {

    override fun mapToSendData(sendArgs: SendArgsDto, message: VkCallbackMessageModel): SendMessageDto {
        val attachments = message.let {
            if (it.isCropped) {
                when (it.chatType) {
                    ChatType.USER -> vkService.findMessageByIdInUserChat(it.id).attachments
                    ChatType.GROUP -> vkService.findMessageByIdInGroupChat(it.peerId, it.conversationMessageId!!).attachments
                }
            }
            else {
                it.attachments
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

    override fun handle(args: String, message: VkCallbackMessageModel) {
        try {
            super.handle(args, message)
        } catch (e: VkApiException) {
            commandHandlerPresenter.failureApi()
        }
    }
}
package io.transferbot.vk.http.bot.input.handler

import io.transferbot.core.application.dto.command.execution.RegisterGroupChatDto
import io.transferbot.core.application.dto.command.execution.RegisterUserDto
import io.transferbot.core.application.dto.command.argument.RegisterArgsDto
import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.shared.http.bot.input.AbstractRegisterCommandHandler
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter
import io.transferbot.vk.http.bot.model.VkCallbackMessageModel

class VKRegisterCommandHandler(
    botOperationUseCase: IBotOperationsInputPort,
    commandHandlerPresenter: CommandHandlerPresenter,
) : AbstractRegisterCommandHandler<VkCallbackMessageModel>(botOperationUseCase, commandHandlerPresenter) {

    override fun mapToRegisterGroupChatData(
        commandArgs: RegisterArgsDto,
        message: VkCallbackMessageModel
    ) = RegisterGroupChatDto(
        message.fromId.toString(),
        commandArgs.chatName,
        message.peerId.toString()
    )

    override fun mapToRegisterUserData(commandArgs: RegisterArgsDto, message: VkCallbackMessageModel) = RegisterUserDto(
        message.fromId.toString(),
        commandArgs.chatName
    )

    override fun getChatType(message: VkCallbackMessageModel) = message.chatType
}
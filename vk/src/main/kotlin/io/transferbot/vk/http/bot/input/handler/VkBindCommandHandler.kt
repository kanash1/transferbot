package io.transferbot.vk.http.bot.input.handler

import io.transferbot.core.application.dto.command.argument.BindArgsDto
import io.transferbot.core.application.dto.command.execution.BindUserDto
import io.transferbot.shared.http.bot.input.AbstractBindCommandHandler
import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter
import io.transferbot.vk.http.bot.model.VkCallbackMessageModel

class VkBindCommandHandler(
    botOperationUseCase: IBotOperationsInputPort,
    commandHandlerPresenter: CommandHandlerPresenter,
): AbstractBindCommandHandler<VkCallbackMessageModel>(botOperationUseCase, commandHandlerPresenter) {

    override fun getChatType(message: VkCallbackMessageModel) = message.chatType

    override fun mapToBindData(bindArgs: BindArgsDto, message: VkCallbackMessageModel) = BindUserDto(
        message.fromId.toString(),
        bindArgs.username,
        bindArgs.verificationCode
    )
}
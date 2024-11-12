package io.transferbot.telegram.http.bot.input.handler

import io.transferbot.core.application.dto.command.argument.BindArgsDto
import io.transferbot.core.application.dto.command.execution.BindUserDto
import io.transferbot.shared.http.bot.input.AbstractBindCommandHandler
import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter
import io.transferbot.telegram.http.bot.model.TelegramMessageModel

class TelegramBindCommandHandler(
    botOperationUseCase: IBotOperationsInputPort,
    commandHandlerPresenter: CommandHandlerPresenter,
): AbstractBindCommandHandler<TelegramMessageModel>(botOperationUseCase, commandHandlerPresenter) {

    override fun getChatType(message: TelegramMessageModel) = message.chatType

    override fun mapToBindData(bindArgs: BindArgsDto, message: TelegramMessageModel) = BindUserDto(
        message.fromId.toString(),
        bindArgs.username,
        bindArgs.verificationCode
    )
}
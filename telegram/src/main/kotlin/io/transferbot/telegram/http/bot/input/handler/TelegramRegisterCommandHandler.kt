package io.transferbot.telegram.http.bot.input.handler

import io.transferbot.core.application.dto.command.execution.RegisterGroupChatDto
import io.transferbot.core.application.dto.command.execution.RegisterUserDto
import io.transferbot.core.application.dto.command.argument.RegisterArgsDto
import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.shared.http.bot.input.AbstractRegisterCommandHandler
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter
import io.transferbot.telegram.http.bot.model.TelegramMessageModel

class TelegramRegisterCommandHandler(
    botOperationUseCase: IBotOperationsInputPort,
    commandHandlerPresenter: CommandHandlerPresenter,
) : AbstractRegisterCommandHandler<TelegramMessageModel>(botOperationUseCase, commandHandlerPresenter) {

    override fun mapToRegisterGroupChatData(
        commandArgs: RegisterArgsDto,
        message: TelegramMessageModel
    ) = RegisterGroupChatDto(
        message.fromId.toString(),
        commandArgs.chatName,
        message.chatId.toString()
    )

    override fun mapToRegisterUserData(commandArgs: RegisterArgsDto, message: TelegramMessageModel) =
        RegisterUserDto(
            message.fromId.toString(),
            commandArgs.chatName
        )

    override fun getChatType(message: TelegramMessageModel) = message.chatType
}
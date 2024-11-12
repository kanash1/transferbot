package io.transferbot.telegram.http.bot.input.handler

import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.shared.http.bot.input.AbstractVerificationCommandHandler
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter
import io.transferbot.telegram.http.bot.model.TelegramMessageModel

class TelegramVerificationCommandHandler(
    botOperationUseCase: IBotOperationsInputPort,
    commandHandlerPresenter: CommandHandlerPresenter,
): AbstractVerificationCommandHandler<TelegramMessageModel>(botOperationUseCase, commandHandlerPresenter) {

    override fun getPlatformUserId(message: TelegramMessageModel) = message.fromId.toString()

    override fun getChatType(message: TelegramMessageModel) = message.chatType
}
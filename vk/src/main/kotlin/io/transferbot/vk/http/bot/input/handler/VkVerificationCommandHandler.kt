package io.transferbot.vk.http.bot.input.handler

import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.shared.http.bot.input.AbstractVerificationCommandHandler
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter
import io.transferbot.vk.http.bot.model.VkCallbackMessageModel

class VkVerificationCommandHandler(
    botOperationUseCase: IBotOperationsInputPort,
    commandHandlerPresenter: CommandHandlerPresenter,
): AbstractVerificationCommandHandler<VkCallbackMessageModel>(botOperationUseCase, commandHandlerPresenter) {

    override fun getPlatformUserId(message: VkCallbackMessageModel) = message.fromId.toString()

    override fun getChatType(message: VkCallbackMessageModel) = message.chatType
}
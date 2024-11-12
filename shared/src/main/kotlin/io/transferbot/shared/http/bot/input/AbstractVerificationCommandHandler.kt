package io.transferbot.shared.http.bot.input

import io.transferbot.core.application.dto.ChatType
import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter

abstract class AbstractVerificationCommandHandler<in Message: Any>(
    private val botOperationsUseCase: IBotOperationsInputPort,
    private val commandHandlerPresenter: CommandHandlerPresenter,
): ICommandHandler<Message> {
    override fun handle(args: String, message: Message) {
        val chatType = getChatType(message)
        if (chatType == ChatType.USER) {
            botOperationsUseCase.createVerificationToken(getPlatformUserId(message))
        } else {
            commandHandlerPresenter.failureChatType(chatType)
        }
    }

    protected abstract fun getPlatformUserId(message: Message): String

    protected abstract fun getChatType(message: Message): ChatType
}
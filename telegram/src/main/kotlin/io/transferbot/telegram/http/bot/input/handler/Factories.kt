package io.transferbot.telegram.http.bot.input.handler

import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.core.application.service.command.parser.SendCommandArgsExtractor
import io.transferbot.shared.http.bot.input.ICommandHandler
import io.transferbot.shared.http.bot.input.ICommandHandlerFactory
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter
import io.transferbot.telegram.http.bot.model.TelegramMessageModel
import io.transferbot.telegram.http.bot.output.ITelegramService
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

abstract class TelegramCommandHandlerFactory(
    private val applicationContext: ApplicationContext
): ICommandHandlerFactory<TelegramMessageModel> {
    protected fun commandHandlerPresenter(chatId: String) =
        applicationContext.getBean("telegramCommandHandlerPresenter", chatId) as CommandHandlerPresenter

    protected fun botOperationsUseCase(chatId: String) =
        applicationContext.getBean("telegramBotOperationsUseCase", chatId) as IBotOperationsInputPort
}

@Component
class TelegramSendCommandHandlerFactory(
    private val telegramService: ITelegramService,
    applicationContext: ApplicationContext
): TelegramCommandHandlerFactory(applicationContext) {

    override val command = "send"

    override fun createHandler(chatId: String): ICommandHandler<TelegramMessageModel> {
        val platformToNames = hashMapOf(
            "vk" to hashSetOf("vk"),
            "telegram" to hashSetOf("tg", "telegram")
        )
        
        return TelegramSendCommandHandler(
            telegramService,
            commandHandlerPresenter(chatId),
            botOperationsUseCase(chatId),
            SendCommandArgsExtractor(platformToNames)
        )
    }
}

@Component
class TelegramRegisterCommandHandlerFactory(
    applicationContext: ApplicationContext
): TelegramCommandHandlerFactory(applicationContext) {

    override val command = "reg"

    override fun createHandler(chatId: String): ICommandHandler<TelegramMessageModel> =
        TelegramRegisterCommandHandler(botOperationsUseCase(chatId), commandHandlerPresenter(chatId))
}

@Component
class TelegramBindCommandHandlerFactory(
    applicationContext: ApplicationContext
): TelegramCommandHandlerFactory(applicationContext) {

    override val command = "bind"

    override fun createHandler(chatId: String): ICommandHandler<TelegramMessageModel> =
        TelegramBindCommandHandler(botOperationsUseCase(chatId), commandHandlerPresenter(chatId))
}

@Component
class TelegramVerificationCommandHandlerFactory(
    applicationContext: ApplicationContext
): TelegramCommandHandlerFactory(applicationContext) {

    override val command = "verif"

    override fun createHandler(chatId: String): ICommandHandler<TelegramMessageModel> =
        TelegramVerificationCommandHandler(botOperationsUseCase(chatId), commandHandlerPresenter(chatId))
}
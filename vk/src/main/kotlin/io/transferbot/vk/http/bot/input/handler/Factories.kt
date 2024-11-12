package io.transferbot.vk.http.bot.input.handler

import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.core.application.service.command.parser.SendCommandArgsExtractor
import io.transferbot.shared.http.bot.input.ICommandHandler
import io.transferbot.shared.http.bot.input.ICommandHandlerFactory
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter
import io.transferbot.vk.http.bot.model.VkCallbackMessageModel
import io.transferbot.vk.http.bot.output.IVkService
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

abstract class VkCommandHandlerFactory(
    private val applicationContext: ApplicationContext
): ICommandHandlerFactory<VkCallbackMessageModel> {
    protected fun commandHandlerPresenter(chatId: String) =
        applicationContext.getBean("vkCommandHandlerPresenter", chatId) as CommandHandlerPresenter

    protected fun botOperationsUseCase(chatId: String) =
        applicationContext.getBean("vkBotOperationsUseCase", chatId) as IBotOperationsInputPort
}

@Component
class VkSendCommandHandlerFactory(
    private val vkService: IVkService,
    applicationContext: ApplicationContext
): VkCommandHandlerFactory(applicationContext) {

    override val command = "send"

    override fun createHandler(chatId: String): ICommandHandler<VkCallbackMessageModel> {
        val platformToNames = hashMapOf(
            "vk" to hashSetOf("vk"),
            "telegram" to hashSetOf("tg", "telegram")
        )
        
        return VkSendCommandHandler(
            vkService,
            commandHandlerPresenter(chatId),
            botOperationsUseCase(chatId),
            SendCommandArgsExtractor(platformToNames)
        )
    }
}

@Component
class VkRegisterCommandHandlerFactory(
    applicationContext: ApplicationContext
): VkCommandHandlerFactory(applicationContext) {

    override val command = "reg"

    override fun createHandler(chatId: String): ICommandHandler<VkCallbackMessageModel> =
        VKRegisterCommandHandler(botOperationsUseCase(chatId), commandHandlerPresenter(chatId))
}

@Component
class VkBindCommandHandlerFactory(
    applicationContext: ApplicationContext
): VkCommandHandlerFactory(applicationContext) {

    override val command = "bind"

    override fun createHandler(chatId: String): ICommandHandler<VkCallbackMessageModel> =
        VkBindCommandHandler(botOperationsUseCase(chatId), commandHandlerPresenter(chatId))
}

@Component
class VkVerificationCommandHandlerFactory(
    applicationContext: ApplicationContext
): VkCommandHandlerFactory(applicationContext) {

    override val command = "verif"

    override fun createHandler(chatId: String): ICommandHandler<VkCallbackMessageModel> =
        VkVerificationCommandHandler(botOperationsUseCase(chatId), commandHandlerPresenter(chatId))
}
package io.transferbot.telegram.config

import io.transferbot.core.application.dto.TransferMessageDto
import io.transferbot.core.application.interactor.BotOperationUseCase
import io.transferbot.core.application.interactor.TransferMessageUseCase
import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.core.application.port.input.ITransferMessageInputPort
import io.transferbot.core.application.port.output.*
import io.transferbot.core.application.service.transfer.DefaultTransferMessageManager
import io.transferbot.core.application.service.transfer.ITransferMessageManager
import io.transferbot.shared.http.bot.output.BotOperationsPresenter
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter
import io.transferbot.shared.http.transfer.output.HttpTransferMessagePresenter
import io.transferbot.shared.http.transfer.output.HttpTransferMessageService
import io.transferbot.telegram.http.bot.tdlib.TelegramApiProperties
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
@EnableConfigurationProperties(TelegramApiProperties::class)
class AppConfig(
    private val telegramUserGateway: IUserPersistenceOutputPort,
    private val telegramGroupChatGateway: IGroupChatPersistenceOutputPort,
    private val telegramPostToPlatformPresenter: IPostToPlatformOutputPort,
    private val verificationTokenPersistenceGateway: IVerificationTokenPersistenceOutputPort,
    @Lazy private val transferManager: ITransferMessageManager<String, TransferMessageDto>,
) {
    @Bean("telegramLocalTransfer")
    @Qualifier("local")
    fun localTelegramTransferMessageUseCase(
        vkUserGateway: IUserPersistenceOutputPort,
        vkGroupChatGateway: IGroupChatPersistenceOutputPort,
        vkPostToPlatformPresenter: IPostToPlatformOutputPort
    ): ITransferMessageInputPort = TransferMessageUseCase(
        vkUserGateway,
        vkGroupChatGateway,
        vkPostToPlatformPresenter
    )

    @Bean("telegramHttpTransfer")
    @Scope("request")
    fun httpTelegramTransferMessageUseCase(
        httpServletResponse: HttpServletResponse,
        jacksonConverter: MappingJackson2HttpMessageConverter,
        vkUserGateway: IUserPersistenceOutputPort,
        vkGroupChatGateway: IGroupChatPersistenceOutputPort,
        vkPostToPlatformPresenter: IPostToPlatformOutputPort
    ): ITransferMessageInputPort = TransferMessageUseCase(
        vkUserGateway,
        vkGroupChatGateway,
        vkPostToPlatformPresenter,
        HttpTransferMessagePresenter(jacksonConverter, httpServletResponse)
    )

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun telegramBotOperationsUseCase(chatId: String): IBotOperationsInputPort = BotOperationUseCase(
        telegramUserGateway,
        telegramGroupChatGateway,
        verificationTokenPersistenceGateway,
        transferManager,
        BotOperationsPresenter(chatId, telegramPostToPlatformPresenter)
    )

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun telegramCommandHandlerPresenter(chatId: String): CommandHandlerPresenter = CommandHandlerPresenter(
        chatId,
        telegramPostToPlatformPresenter
    )

    @Bean
    fun transferManager(
        @Qualifier("local")
        @Lazy
        transferUseCase: ITransferMessageInputPort,
        transferMessageService: HttpTransferMessageService
    ): ITransferMessageManager<String, TransferMessageDto> = DefaultTransferMessageManager().apply {
        setPlatformTransfer("telegram", transferUseCase::transfer)
        setPlatformTransfer("vk") { transferMessageService.transferFile(it, "vk") }
    }

}
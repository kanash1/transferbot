package io.transferbot.vk.config

import com.vk.api.sdk.client.GsonHolder
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.httpclient.HttpTransportClient
import io.transferbot.core.application.dto.TransferMessageDto
import io.transferbot.core.application.interactor.BotOperationUseCase
import io.transferbot.core.application.interactor.TransferMessageUseCase
import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.core.application.port.input.ITransferMessageInputPort
import io.transferbot.core.application.port.output.IGroupChatPersistenceOutputPort
import io.transferbot.core.application.port.output.IPostToPlatformOutputPort
import io.transferbot.core.application.port.output.IUserPersistenceOutputPort
import io.transferbot.core.application.port.output.IVerificationTokenPersistenceOutputPort
import io.transferbot.core.application.service.transfer.DefaultTransferMessageManager
import io.transferbot.core.application.service.transfer.ITransferMessageManager
import io.transferbot.shared.http.bot.output.BotOperationsPresenter
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter
import io.transferbot.shared.http.transfer.output.HttpTransferMessagePresenter
import io.transferbot.shared.http.transfer.output.HttpTransferMessageService
import io.transferbot.vk.http.bot.VkApiProperties
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
@EnableConfigurationProperties(VkApiProperties::class)
class AppConfig(
    private val vkUserGateway: IUserPersistenceOutputPort,
    private val vkGroupChatGateway: IGroupChatPersistenceOutputPort,
    @Lazy private val vkPostToPlatformPresenter: IPostToPlatformOutputPort,
    private val verificationTokenPersistenceGateway: IVerificationTokenPersistenceOutputPort,
    @Lazy private val transferManager: ITransferMessageManager<String, TransferMessageDto>,
) {

    @Bean
    fun gsonHolder() = GsonHolder()

    @Bean
    fun vkApiClient() = VkApiClient(HttpTransportClient())

    @Bean("vkLocalTransfer")
    @Qualifier("local")
    fun localVkTransferMessageUseCase(
        vkUserGateway: IUserPersistenceOutputPort,
        vkGroupChatGateway: IGroupChatPersistenceOutputPort,
        vkPostToPlatformPresenter: IPostToPlatformOutputPort
    ): ITransferMessageInputPort = TransferMessageUseCase(
        vkUserGateway,
        vkGroupChatGateway,
        vkPostToPlatformPresenter
    )

    @Bean("vkHttpTransfer")
    @Scope("request")
    fun httpTransferMessageUseCase(
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
    fun vkBotOperationsUseCase(chatId: String): IBotOperationsInputPort = BotOperationUseCase(
        vkUserGateway,
        vkGroupChatGateway,
        verificationTokenPersistenceGateway,
        transferManager,
        BotOperationsPresenter(chatId, vkPostToPlatformPresenter)
    )

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun vkCommandHandlerPresenter(chatId: String): CommandHandlerPresenter = CommandHandlerPresenter(
        chatId,
        vkPostToPlatformPresenter
    )

    @Bean
    fun transferManager(
        @Qualifier("local")
        @Lazy
        transferUseCase: ITransferMessageInputPort,
        transferMessageService: HttpTransferMessageService
    ): ITransferMessageManager<String, TransferMessageDto> = DefaultTransferMessageManager().apply {
        setPlatformTransfer("vk", transferUseCase::transfer)
        setPlatformTransfer("telegram") { transferMessageService.transfer(it, "telegram") }
    }
}
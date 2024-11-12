package io.transferbot.telegram.http.bot.tdlib

import io.github.oshai.kotlinlogging.KotlinLogging
import io.transferbot.telegram.http.bot.input.TelegramUpdateController
import it.tdlight.Init
import it.tdlight.Log
import it.tdlight.client.*
import it.tdlight.jni.TdApi
import it.tdlight.tdnative.NativeClient
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import kotlin.io.path.Path

@Component
class TdLib(
    private val tgApiProperties: TelegramApiProperties,
    private val updateController: TelegramUpdateController,
) {
    private val log = KotlinLogging.logger { }
    private val coroutineScope = CoroutineScope(SupervisorJob())
    private var _client: SimpleTelegramClient? = null
    val client: SimpleTelegramClient get() = _client!!

    init {
        Init.init()
        Log.setLogMessageHandler(1, KotlinLogMessageHandler())
    }

    @PostConstruct
    private fun init() {
        coroutineScope.launch {
            val clientFactory = SimpleTelegramClientFactory()
            clientFactory.use {
                val apiToken = APIToken(tgApiProperties.appId!!, tgApiProperties.appHash)
                val settings = TDLibSettings.create(apiToken)
                val sessionPath = Path(tgApiProperties.sessionPath)
                settings.databaseDirectoryPath = sessionPath.resolve("data")
                settings.downloadedFilesDirectoryPath = sessionPath.resolve("downloads")
                val clientBuilder = clientFactory.builder(settings)
                val authenticationData = AuthenticationSupplier.bot(tgApiProperties.botToken)
                clientBuilder.addUpdateHandler<TdApi.UpdateAuthorizationState>(::onUpdateAuthorizationState)
                clientBuilder.addUpdateHandler<TdApi.UpdateNewMessage>(updateController::onUpdate)
                _client = clientBuilder.build(authenticationData)
            }
        }
    }

    @PreDestroy
    private fun destroy() {
        _client?.close()
        coroutineScope.cancel()
    }

    private fun onUpdateAuthorizationState(update: TdApi.UpdateAuthorizationState) {
        when (val authorizationState: TdApi.AuthorizationState = update.authorizationState) {
            is TdApi.AuthorizationStateReady -> log.debug { "Logged in" }
            is TdApi.AuthorizationStateClosing -> log.debug { "Closing..." }
            is TdApi.AuthorizationStateClosed -> log.debug { "Closed" }
            is TdApi.AuthorizationStateLoggingOut -> log.debug { "Logging out..." }
            else -> log.debug { "Unsupported authorization state: $authorizationState" }
        }
    }

    private inline fun <reified T : TdApi.Update> MutableTelegramClient.addUpdateHandler(handler: GenericUpdateHandler<in T>) {
        addUpdateHandler(T::class.java, handler)
    }

    private class KotlinLogMessageHandler: NativeClient.LogMessageHandler {
        private val log = KotlinLogging.logger {  }

        override fun onLogMessage(verbosityLevel: Int, message: String?) {
            when (verbosityLevel) {
                0 -> {}
                1 -> log.error { message }
                2 -> log.warn { message }
                3 -> log.info { message }
                4 -> log.debug { message }
                else -> log.trace { message }
            }
        }
    }
}
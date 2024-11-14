package io.transferbot.telegram.http.bot.input

import io.github.oshai.kotlinlogging.KotlinLogging
import io.transferbot.core.application.dto.ChatType
import io.transferbot.core.application.service.command.parser.splitTextIntoCommandAndArgs
import io.transferbot.shared.http.bot.input.ICommandHandlerFactory
import io.transferbot.telegram.http.bot.model.TelegramMessageModel
import io.transferbot.telegram.http.bot.tdlib.toDto
import it.tdlight.jni.TdApi
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class TelegramUpdateController(
    commandHandlerFactories: List<ICommandHandlerFactory<TelegramMessageModel>>
) {
    private val log = KotlinLogging.logger { }
    private val coroutineScope = CoroutineScope(SupervisorJob())
    private val commandToHandlerFactory = hashMapOf<String, ICommandHandlerFactory<TelegramMessageModel>>()

    init {
        for (factory in commandHandlerFactories) {
            commandToHandlerFactory[factory.command] = factory
        }
    }

    fun onUpdate(update: TdApi.UpdateNewMessage) {
        log.info { update }
        coroutineScope.launch {
            try {
                val tgMessageDto = update.message.toDto()

                with(tgMessageDto) {
                    val (command, commandArgs) = splitTextIntoCommandAndArgs(
                        when (tgMessageDto.chatType) {
                            ChatType.USER -> text
                            ChatType.GROUP -> text.substring(text.indexOf('/'))
                        }
                    )

                    commandToHandlerFactory[command]!!.createHandler(chatId.toString()).handle(commandArgs, this)
                }
            } catch (e: Exception) {
                log.info { e.message }
            }
        }
    }

    @PreDestroy
    private fun destroy() {
        coroutineScope.cancel()
    }
}
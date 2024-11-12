package io.transferbot.vk.http.bot.input

import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.vk.api.sdk.client.GsonHolder
import com.vk.api.sdk.events.Events
import com.vk.api.sdk.events.callback.CallbackApi
import com.vk.api.sdk.objects.callback.*
import com.vk.api.sdk.objects.callback.messages.CallbackMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import io.transferbot.core.application.dto.ChatType
import io.transferbot.core.application.service.command.parser.splitTextIntoCommandAndArgs
import io.transferbot.shared.http.bot.input.ICommandHandlerFactory
import io.transferbot.vk.http.bot.VkApiProperties
import io.transferbot.vk.http.bot.model.VkCallbackMessageModel
import io.transferbot.vk.http.bot.output.IVkService
import io.transferbot.vk.http.toModel
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import org.springframework.stereotype.Service


@Service
class VkCallbackApi(
    private val gsonHolder: GsonHolder,
    vkService: IVkService,
    props: VkApiProperties,
    commandHandlerFactories: List<ICommandHandlerFactory<VkCallbackMessageModel>>
): CallbackApi(vkService.getCallbackConfirmationCode(), props.secretKey) {
    private val log = KotlinLogging.logger {  }
    private val coroutineScope = CoroutineScope(SupervisorJob())
    private val commandToHandlerFactory = hashMapOf<String, ICommandHandlerFactory<VkCallbackMessageModel>>()

    init {
        for (factory in commandHandlerFactories) {
            commandToHandlerFactory[factory.command] = factory
        }
    }

    override fun parse(message: CallbackMessage): String {
        try {
            return when (message.type) {
                Events.CONFIRMATION -> confirmation()
                else -> super.parse(message)
            }
        } catch (e: JsonParseException) {
            log.error { e.message }
        }

        return "OK"
    }

    fun parseMessage(json: String): String {
        try {
            return super.parseMessage(gsonHolder.gson.fromJson(json, CallbackMessage::class.java))
        } catch (e: JsonSyntaxException) {
            log.error { e.message }
        }

        return "OK"
    }

    override fun messageNew(groupId: Int, event: MessageNew) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                with (event.`object`.message) {
                    val chatType = IVkService.getChatType(peerId)

                    val (command, commandArgs) = splitTextIntoCommandAndArgs(when (chatType) {
                        ChatType.USER -> text
                        ChatType.GROUP -> text.substring(text.indexOf('/'))
                    })

                    commandToHandlerFactory[command]!!.createHandler(peerId.toString()).handle(commandArgs, toModel())
                }
            } catch (e: Throwable) {
                log.error { e }
            }
        }
    }

    @PreDestroy
    fun cancelScope() {
        coroutineScope.cancel()
    }
}
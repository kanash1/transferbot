package io.transferbot.shared.http.transfer.output

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.transferbot.core.application.dto.AttachmentType
import io.transferbot.core.application.dto.TransferMessageDto
import io.transferbot.core.application.exception.TransferException
import io.transferbot.core.application.exception.UnknownValueException
import io.transferbot.shared.http.transfer.model.MultipartTransferMessage
import io.transferbot.shared.http.transfer.model.toModel
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class HttpTransferMessageService(
    private val webClient: WebClient,
    private val objectMapper: ObjectMapper
) {
    private val log = KotlinLogging.logger {  }

    companion object Api {
        private const val TELEGRAM_BOT_URI = "http://localhost:8083/tg/transfer/messages"
        private const val VK_BOT_URI = "http://localhost:8082/vk/transfer/messages"
    }

    fun transfer(transferMessageRequestDto: TransferMessageDto, platform: String) {
        webClient.post()
            .apply {
                when (platform) {
                    "vk" -> uri(VK_BOT_URI)
                    "telegram" -> uri(TELEGRAM_BOT_URI)
                    else -> throw UnknownValueException("Unknown platform to transfer $platform")
                }
            }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(transferMessageRequestDto.toModel())
            .retrieve()
            .bodyToMono<String>()
            .doOnError {
                log.debug { it.message }
                throw TransferException(it.message, it)
            }
            .block()!!
    }

    fun transferFile(transferMessageRequestDto: TransferMessageDto, platform: String) {
        val multipartBodyBuilder = MultipartBodyBuilder()
        val multipartTransferMessage = transferMessageRequestDto.run {
            MultipartTransferMessage(userTransferId, text, chatType.name.lowercase(), chatName)
        }
        multipartBodyBuilder
            .part("message", objectMapper.writeValueAsString(multipartTransferMessage), MediaType.TEXT_PLAIN)
        for ((idx, attachment) in transferMessageRequestDto.attachments.withIndex()) {
            when (attachment.type) {
                AttachmentType.PHOTO -> {
                    multipartBodyBuilder
                        .part("file$idx", attachment.file!!, MediaType.IMAGE_JPEG)
                        .filename(attachment.name ?: "")
                }
                AttachmentType.FILE -> {
                    multipartBodyBuilder
                        .part("file$idx", attachment.file!!)
                        .filename(attachment.name ?: "")
                }
            }
        }

        webClient.post()
            .apply {
                when (platform) {
                    "telegram" -> uri(VK_BOT_URI)
                    "vk" -> uri(VK_BOT_URI)
                    else -> throw UnknownValueException("Unknown platform to transfer $platform")
                }
            }
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(multipartBodyBuilder.build())
            .retrieve()
            .bodyToMono<String>()
            .doOnError { log.error { it.message } }
            .block()!!
    }
}
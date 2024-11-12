package io.transferbot.shared.http.transfer.input

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.transferbot.core.application.port.input.ITransferMessageInputPort
import io.transferbot.shared.http.transfer.model.MultipartTransferMessageModel
import io.transferbot.shared.http.transfer.model.TransferMessageRequestModel
import io.transferbot.shared.http.transfer.model.toDto
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

abstract class AbstractTransferController(
    private val objectMapper: ObjectMapper
) {
    private val log = KotlinLogging.logger {  }

    @PostMapping("/messages", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun postMessage(@Valid @RequestBody transferMessageRequestModel: TransferMessageRequestModel) {
        log.debug { "Received request: $transferMessageRequestModel" }

        if (transferMessageRequestModel.text.isNotEmpty() || transferMessageRequestModel.attachments.isNotEmpty()) {
            val transferMessageUseCase = getTransferMessageUseCase()
            transferMessageUseCase.transfer(transferMessageRequestModel.toDto())
        }
    }

    @PostMapping("/messages", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun postMessageWithFiles(@ModelAttribute multipartTransferMessageModel: MultipartTransferMessageModel) {
        log.debug { "Received request: $multipartTransferMessageModel" }

        val transferMessageDto = multipartTransferMessageModel.toDto(objectMapper)
        if (transferMessageDto.text.isNotEmpty() || transferMessageDto.attachments.isNotEmpty()) {
            val transferMessageUseCase = getTransferMessageUseCase()
            transferMessageUseCase.transfer(transferMessageDto)
        }
    }

    protected abstract fun getTransferMessageUseCase(): ITransferMessageInputPort
}
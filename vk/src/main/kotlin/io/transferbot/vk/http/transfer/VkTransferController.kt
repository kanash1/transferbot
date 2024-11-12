package io.transferbot.vk.http.transfer

import com.fasterxml.jackson.databind.ObjectMapper
import io.transferbot.core.application.port.input.ITransferMessageInputPort
import io.transferbot.shared.http.transfer.input.AbstractTransferController
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/vk/transfer")
class VkTransferController(
    private val context: ApplicationContext,
    objectMapper: ObjectMapper
) : AbstractTransferController(objectMapper) {

    override fun getTransferMessageUseCase(): ITransferMessageInputPort =
        context.getBean<ITransferMessageInputPort>("vkHttpTransfer")
}
package io.transferbot.core.application.service.transfer

import io.transferbot.core.application.dto.TransferMessageDto
import io.transferbot.core.application.exception.UnknownValueException

open class DefaultTransferMessageManager:
    ITransferMessageManager<String, TransferMessageDto> {
    private val platformToTransfer: MutableMap<String, (TransferMessageDto) -> Unit> = hashMapOf()

    override fun transfer(platform: String, transferMessage: TransferMessageDto) {
        platformToTransfer[platform]?.invoke(transferMessage)
            ?: throw UnknownValueException("There is no transfer for the platform $platform")
    }

    override fun setPlatformTransfer(platform: String, handler: (TransferMessageDto) -> Unit) {
        platformToTransfer[platform] = handler
    }

    override fun removePlatformTransfer(platform: String) {
        platformToTransfer.remove(platform)
    }
}
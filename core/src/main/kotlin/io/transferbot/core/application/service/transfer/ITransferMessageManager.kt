package io.transferbot.core.application.service.transfer

interface ITransferMessageManager<Platform: Any, TransferMessage: Any> {
    fun transfer(platform: Platform, transferMessage: TransferMessage)
    fun setPlatformTransfer(platform: Platform, handler: (TransferMessage) -> Unit)
    fun removePlatformTransfer(platform: Platform)
}
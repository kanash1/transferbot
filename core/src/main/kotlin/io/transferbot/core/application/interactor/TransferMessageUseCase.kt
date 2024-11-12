package io.transferbot.core.application.interactor

import io.transferbot.core.application.dto.ChatType
import io.transferbot.core.application.dto.PostToPlatformDto
import io.transferbot.core.application.dto.TransferMessageDto
import io.transferbot.core.application.exception.EntityNotFoundException
import io.transferbot.core.application.exception.TransferException
import io.transferbot.core.application.port.input.ITransferMessageInputPort
import io.transferbot.core.application.port.output.IGroupChatPersistenceOutputPort
import io.transferbot.core.application.port.output.IPostToPlatformOutputPort
import io.transferbot.core.application.port.output.ITransferMessageOutputPort
import io.transferbot.core.application.port.output.IUserPersistenceOutputPort

class TransferMessageUseCase(
    private val userPersistenceOutputPort: IUserPersistenceOutputPort,
    private val groupChatPersistenceOutputPort: IGroupChatPersistenceOutputPort,
    private val postToPlatformOutputPort: IPostToPlatformOutputPort,
    private val transferPresenter: ITransferMessageOutputPort? = null
) : ITransferMessageInputPort {
    override fun transfer(transferMessageDto: TransferMessageDto) {
        try {
            val chatId = transferMessageDto.run {
                when (chatType) {
                    ChatType.USER -> userPersistenceOutputPort.findUserByTransferId(userTransferId).platformId
                        ?: throw EntityNotFoundException("User not found")

                    ChatType.GROUP -> groupChatPersistenceOutputPort.findChatByUserTransferIdAndChatName(
                        userTransferId, chatName!!
                    ).platformId
                }
            }
            postToPlatformOutputPort.postToPlatform(
                PostToPlatformDto(
                    chatId,
                    transferMessageDto.text,
                    transferMessageDto.attachments
                )
            )
            transferPresenter?.success()
        } catch (e: EntityNotFoundException) {
            transferPresenter?.failure(e.message ?: "Cannot transfer")
                ?: throw TransferException(e.message, e)
        }
    }
}
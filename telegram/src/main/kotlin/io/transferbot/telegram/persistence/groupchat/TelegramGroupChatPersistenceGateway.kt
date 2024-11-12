package io.transferbot.telegram.persistence.groupchat

import io.transferbot.core.application.exception.EntityExistsException
import io.transferbot.core.application.exception.EntityNotFoundException
import io.transferbot.core.application.port.output.IGroupChatPersistenceOutputPort
import io.transferbot.core.domain.entity.GroupChat
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

@Component
class TelegramGroupChatPersistenceGateway(private val repository: ITelegramGroupChatJpaRepository) :
    IGroupChatPersistenceOutputPort {

    override fun save(groupChat: GroupChat) = try {
        repository.save(groupChat.toEntity()).toDomain()
    } catch (e: DataIntegrityViolationException) {
        throw EntityExistsException(e.message, e)
    }

    override fun findChatByUserTransferIdAndChatName(userTransferId: Long, name: String): GroupChat =
        repository.findByUserTransferIdAndName(userTransferId, name)?.toDomain()
            ?: throw EntityNotFoundException("Group chat not found")
}
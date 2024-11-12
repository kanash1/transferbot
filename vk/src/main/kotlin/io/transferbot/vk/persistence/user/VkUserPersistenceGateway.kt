package io.transferbot.vk.persistence.user

import io.transferbot.core.application.exception.EntityExistsException
import io.transferbot.core.application.exception.EntityNotFoundException
import io.transferbot.core.application.port.output.IUserPersistenceOutputPort
import io.transferbot.core.domain.entity.User
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class VkUserPersistenceGateway(private val repository: IUserJpaRepository): IUserPersistenceOutputPort {
    override fun save(user: User) = try {
        repository.save(user.toEntity()).toDomain()
    } catch (e: DataIntegrityViolationException) {
        throw EntityExistsException(e.message, e)
    }

    override fun findUserByTransferId(transferId: Long): User =
        repository.findById(transferId).getOrNull()?.toDomain() ?: throw EntityNotFoundException("User not found")

    override fun findUserByTransferName(transferName: String): User =
        repository.findByTransferName(transferName)?.toDomain() ?: throw EntityNotFoundException("User not found")

    override fun findUserByPlatformId(platformId: String): User =
        repository.findByVkUserId(platformId.toLong())?.toDomain() ?: throw EntityNotFoundException("User not found")
}
package io.transferbot.shared.persistence.verification

import io.transferbot.core.application.exception.EntityNotFoundException
import io.transferbot.core.application.port.output.IVerificationTokenPersistenceOutputPort
import io.transferbot.core.domain.entity.VerificationToken
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class VerificationPersistenceGateway(
    private val repository: IVerificationTokenJpaRepository
) : IVerificationTokenPersistenceOutputPort {
    override fun save(verificationToken: VerificationToken): VerificationToken =
        repository.save(verificationToken.toEntity()).toDomain()

    override fun findVerificationTokenByUserTransferId(userTransferId: Long): VerificationToken =
        repository.findById(userTransferId).getOrNull()?.toDomain()
            ?: throw EntityNotFoundException("No token found for user with id $userTransferId")
}
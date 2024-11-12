package io.transferbot.shared.persistence.verification

import io.transferbot.core.application.exception.EntityExistsException
import io.transferbot.core.application.exception.EntityNotFoundException
import io.transferbot.core.application.port.output.IVerificationTokenPersistenceOutputPort
import io.transferbot.core.domain.entity.VerificationToken

class InMemoryVerificationPersistenceGateway : IVerificationTokenPersistenceOutputPort {
    private val tokens: MutableMap<Long, VerificationToken> = HashMap()

    override fun save(verificationToken: VerificationToken): VerificationToken {
        if (tokens.filter { it.value.userTransferId == verificationToken.userTransferId }.isEmpty()) {
            tokens[verificationToken.userTransferId] = verificationToken
            return verificationToken.apply { VerificationToken(userTransferId, expiryDate, token) }
        } else throw EntityExistsException("Token exist")
    }

    override fun findVerificationTokenByUserTransferId(userTransferId: Long): VerificationToken {
        return tokens.values.filter { it.userTransferId == userTransferId }.getOrNull(0)
            ?: throw EntityNotFoundException("Token not found")
    }
}
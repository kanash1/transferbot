package io.transferbot.shared.persistence.verification

import io.transferbot.core.domain.entity.VerificationToken
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "verification_token")
class VerificationTokenEntity(
    @Column(name = "token_expiry_date", nullable = false)
    val tokenExpiryDate: Date,
    @Column(name = "token_value", nullable = false)
    val tokenValue: String,
    @Id
    @Column(name = "user_transfer_id", nullable = false, unique = true)
    val userTransferId: Long
)

fun VerificationToken.toEntity() = VerificationTokenEntity(
    expiryDate,
    token,
    userTransferId
)

fun VerificationTokenEntity.toDomain() = VerificationToken(
    userTransferId,
    tokenExpiryDate,
    tokenValue
)
package io.transferbot.telegram.persistence.user

import io.transferbot.core.domain.entity.User
import jakarta.persistence.*

@Entity
@Table(name = "user_transfer")
class UserEntity(
    @Column(name = "user_transfer_name", nullable = false, unique = true)
    val transferName: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_transfer_id", nullable = false, unique = true)
    val transferId: Long? = null,
    @Column(name = "telegram_user_id", nullable = true, unique = true)
    val telegramUserId: Long? = null
)

fun User.toEntity() = UserEntity(
    transferName = transferName,
    transferId = transferId,
    telegramUserId = platformId?.toLong()
)


fun UserEntity.toDomain() = User(
    platformId = telegramUserId?.toString(),
    transferName = transferName,
    transferId = transferId
)
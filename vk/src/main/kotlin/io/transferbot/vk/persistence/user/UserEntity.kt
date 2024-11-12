package io.transferbot.vk.persistence.user

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
    @Column(name = "vk_user_id", nullable = true, unique = true)
    val vkUserId: Long? = null
)

fun User.toEntity() = UserEntity(
    transferName = transferName,
    transferId = transferId,
    vkUserId = platformId?.toLong()
)


fun UserEntity.toDomain() = User(
    platformId = vkUserId?.toString(),
    transferName = transferName,
    transferId = transferId
)
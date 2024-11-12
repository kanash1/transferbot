package io.transferbot.telegram.persistence.groupchat

import io.transferbot.core.domain.entity.GroupChat
import io.transferbot.telegram.persistence.user.UserEntity
import io.transferbot.telegram.persistence.user.toDomain
import io.transferbot.telegram.persistence.user.toEntity
import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(
    name = "telegram_group_chat",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_transfer_id", "telegram_group_chat_id", "telegram_group_chat_name"])]
)
class TelegramGroupChatEntity(
    @Column(name = "telegram_group_chat_name", nullable = false)
    val name: String,
    @EmbeddedId
    val key: VkGroupChatKey,
    @ManyToOne
    @MapsId("userTransferId")
    @JoinColumn(name = "user_transfer_id")
    val user: UserEntity,
)

fun TelegramGroupChatEntity.toDomain() = GroupChat(
    name = name,
    platformId = key.telegramGroupChatId.toString(),
    user = user.toDomain()
)

fun GroupChat.toEntity() = TelegramGroupChatEntity(
    name = name,
    key = VkGroupChatKey(user.platformId!!.toLong(), platformId.toLong()),
    user = user.toEntity()
)

@Embeddable
data class VkGroupChatKey(
    @Column(name = "user_transfer_id", nullable = false)
    val userTransferId: Long,
    @Column(name = "telegram_group_chat_id", nullable = false)
    val telegramGroupChatId: Long,
) : Serializable
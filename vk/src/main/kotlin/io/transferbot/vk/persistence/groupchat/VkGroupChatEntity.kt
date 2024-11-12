package io.transferbot.vk.persistence.groupchat

import io.transferbot.core.domain.entity.GroupChat
import io.transferbot.vk.persistence.user.UserEntity
import io.transferbot.vk.persistence.user.toEntity
import io.transferbot.vk.persistence.user.toDomain
import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(
    name = "vk_group_chat",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_transfer_id", "vk_group_chat_id", "vk_group_chat_name"])]
)
class VkGroupChatEntity(
    @Column(name = "vk_group_chat_name", nullable = false)
    val name: String,
    @EmbeddedId
    val key: VkGroupChatKey,
    @ManyToOne
    @MapsId("userTransferId")
    @JoinColumn(name = "user_transfer_id")
    val user: UserEntity,
)

fun VkGroupChatEntity.toDomain() = GroupChat(name, key.vkGroupChatId.toString(),  user.toDomain())

fun GroupChat.toEntity() = VkGroupChatEntity(
    name,
    VkGroupChatKey(user.platformId!!.toLong(), platformId.toLong()),
    user.toEntity()
)

@Embeddable
data class VkGroupChatKey(
    @Column(name = "user_transfer_id", nullable = false)
    val userTransferId: Long,
    @Column(name = "vk_group_chat_id", nullable = false)
    val vkGroupChatId: Long,
) : Serializable
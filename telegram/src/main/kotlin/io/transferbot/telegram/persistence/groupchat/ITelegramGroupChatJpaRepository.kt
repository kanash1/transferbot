package io.transferbot.telegram.persistence.groupchat

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ITelegramGroupChatJpaRepository: JpaRepository<TelegramGroupChatEntity, VkGroupChatKey> {
    @Query("select e from TelegramGroupChatEntity e where e.key.userTransferId=?1 and e.name=?2")
    fun findByUserTransferIdAndName(userTransferId: Long, name: String): TelegramGroupChatEntity?
    @Query("delete from TelegramGroupChatEntity e where e.user.telegramUserId=?1 and e.name=?2")
    fun deleteByTelegramUserIdAndName(vkUserId: Long, name: String): Boolean
}

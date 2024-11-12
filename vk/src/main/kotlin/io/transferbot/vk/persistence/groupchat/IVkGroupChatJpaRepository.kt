package io.transferbot.vk.persistence.groupchat

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IVkGroupChatJpaRepository: JpaRepository<VkGroupChatEntity, VkGroupChatKey> {
    @Query("select e from VkGroupChatEntity e where e.key.userTransferId=?1 and e.name=?2")
    fun findByUserTransferIdAndName(userTransferId: Long, name: String): VkGroupChatEntity?
    @Query("delete from VkGroupChatEntity e where e.user.vkUserId=?1 and e.name=?2")
    fun deleteByVkUserIdAndName(vkUserId: Long, name: String): Boolean
}

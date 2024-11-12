package io.transferbot.telegram.persistence.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IUserJpaRepository: JpaRepository<UserEntity, Long> {
    fun findByTransferName(transferName: String): UserEntity?
    fun findByTelegramUserId(tgUserId: Long): UserEntity?
}
package io.transferbot.shared.persistence.verification

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IVerificationTokenJpaRepository: JpaRepository<VerificationTokenEntity, Long>
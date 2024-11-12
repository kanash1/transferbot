package io.transferbot.core.domain.entity

import io.transferbot.core.domain.entity.VerificationToken.Companion.TOKEN_SIZE
import java.util.*
import kotlin.random.Random

class VerificationToken(
    val userTransferId: Long,
    val expiryDate: Date = Date(Date().time + EXPIRATION_MS),
    val token: String = generateToken(),
) {
    companion object {
        const val EXPIRATION_MS: Long = 60000
        const val TOKEN_SIZE = 6
    }

    fun isTokenValid(token: String): Boolean = this.token == token && expiryDate.after(Date())
}

private fun generateToken(): String {
    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..TOKEN_SIZE)
        .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
        .joinToString("")
}
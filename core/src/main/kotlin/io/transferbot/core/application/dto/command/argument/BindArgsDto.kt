package io.transferbot.core.application.dto.command.argument

import io.transferbot.core.application.exception.ObjectBuildException

data class BindArgsDto(
    val username: String,
    val verificationCode: String
) {
    class Builder {
        private var _username: String? = null
        private var _verificationCode: String? = null

        fun username(username: String) = apply { _username = username }
        fun verificationCode(verificationCode: String) = apply { _verificationCode = verificationCode }

        fun username() = _username
        fun verificationCode() = _verificationCode

        fun build() = try {
            BindArgsDto(_username!!, _verificationCode!!)
        } catch (e: NullPointerException) {
            throw ObjectBuildException("Not all required fields are filled in")
        }
    }
}
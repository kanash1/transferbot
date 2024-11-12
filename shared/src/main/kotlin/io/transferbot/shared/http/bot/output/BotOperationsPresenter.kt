package io.transferbot.shared.http.bot.output

import io.github.oshai.kotlinlogging.KotlinLogging
import io.transferbot.core.application.dto.PostToPlatformDto
import io.transferbot.core.application.dto.command.execution.BindUserDto
import io.transferbot.core.application.dto.command.execution.RegisterGroupChatDto
import io.transferbot.core.application.dto.command.execution.RegisterUserDto
import io.transferbot.core.application.dto.command.execution.SendMessageDto
import io.transferbot.core.application.exception.EntityExistsException
import io.transferbot.core.application.exception.EntityNotFoundException
import io.transferbot.core.application.exception.TokenValidationException
import io.transferbot.core.application.exception.TransferException
import io.transferbot.core.application.port.output.IBotOperationsOutputPort
import io.transferbot.core.application.port.output.IPostToPlatformOutputPort

class BotOperationsPresenter(
    private val chatId: String,
    private val postToPlatformPresenter: IPostToPlatformOutputPort
) : IBotOperationsOutputPort {

    private val log = KotlinLogging.logger { }

    private fun postTextMessage(message: String) {
        postToPlatformPresenter.postToPlatform(PostToPlatformDto(chatId, message))
    }

    override fun failureSendMessage(e: Throwable, sendMessageDto: SendMessageDto) {
        log.debug { e.message }
        when (e) {
            is EntityNotFoundException -> postTextMessage("Register to use command")
            is TransferException -> postTextMessage("Cannot send message")
        }
    }

    override fun successRegisterUser() {
        postTextMessage("Register user chat successful")
    }

    override fun failureRegisterUser(e: Throwable, registerUserDto: RegisterUserDto) {
        log.debug { e.message }
        when (e) {
            is EntityExistsException -> postTextMessage("You already registered or user with name \"${registerUserDto.username}\" already exists")
        }
    }

    override fun successRegisterGroupChat() {
        postTextMessage("Register group chat successful")
    }

    override fun failureRegisterGroupChat(e: Throwable, registerGroupChatDto: RegisterGroupChatDto) {
        log.debug { e.message }
        when (e) {
            is EntityExistsException -> postTextMessage("Chat with name \"${registerGroupChatDto.groupChatName}\" already exists")
            is EntityNotFoundException -> postTextMessage("Register to use command")
        }
    }

    override fun successCreateVerificationToken(token: String) {
        postTextMessage("Verification token: $token")
    }

    override fun failureCreateVerificationToken(e: Throwable) {
        log.debug { e.message }
        if (e is EntityNotFoundException)
            postTextMessage("Register to use command")
    }

    override fun successBindUser() {
        postTextMessage("Bind successful")
    }

    override fun failureBindUser(e: Throwable, bindUserDto: BindUserDto) {
        log.debug { e.message }
        when (e) {
            is EntityExistsException -> postTextMessage("User with name \"${bindUserDto.transferName}\" already have registered to this platform")
            is EntityNotFoundException -> postTextMessage("User with name \"${bindUserDto.transferName}\" not found")
            is TokenValidationException -> postTextMessage("Invalid token")
        }
    }
}
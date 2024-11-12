package io.transferbot.core.application.port.output

import io.transferbot.core.application.dto.PostToPlatformDto
import io.transferbot.core.application.dto.command.execution.BindUserDto
import io.transferbot.core.application.dto.command.execution.RegisterGroupChatDto
import io.transferbot.core.application.dto.command.execution.RegisterUserDto
import io.transferbot.core.application.dto.command.execution.SendMessageDto
import io.transferbot.core.domain.entity.GroupChat
import io.transferbot.core.domain.entity.User
import io.transferbot.core.domain.entity.VerificationToken

interface IGroupChatPersistenceOutputPort {
    fun save(groupChat: GroupChat): GroupChat
    fun findChatByUserTransferIdAndChatName(userTransferId: Long, name: String): GroupChat
}

interface IUserPersistenceOutputPort {
    fun save(user: User): User
    fun findUserByTransferId(transferId: Long): User
    fun findUserByTransferName(transferName: String): User
    fun findUserByPlatformId(platformId: String): User
}

interface IVerificationTokenPersistenceOutputPort {
    fun save(verificationToken: VerificationToken): VerificationToken
    fun findVerificationTokenByUserTransferId(userTransferId: Long): VerificationToken
}

interface ITransferMessageOutputPort {
    fun success()
    fun failure(errorMessage: String)
}

interface IPostToPlatformOutputPort {
    fun postToPlatform(postToPlatformDto: PostToPlatformDto)
}

interface IBotOperationsOutputPort {
    fun failureSendMessage(e: Throwable, sendMessageDto: SendMessageDto)

    fun successRegisterUser()
    fun failureRegisterUser(e: Throwable, registerUserDto: RegisterUserDto)

    fun successRegisterGroupChat()
    fun failureRegisterGroupChat(e: Throwable, registerGroupChatDto: RegisterGroupChatDto)

    fun successCreateVerificationToken(token: String)
    fun failureCreateVerificationToken(e: Throwable)

    fun successBindUser()
    fun failureBindUser(e: Throwable, bindUserDto: BindUserDto)
}
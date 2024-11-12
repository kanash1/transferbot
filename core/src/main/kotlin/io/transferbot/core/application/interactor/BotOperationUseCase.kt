package io.transferbot.core.application.interactor

import io.transferbot.core.application.dto.TransferMessageDto
import io.transferbot.core.application.dto.command.execution.BindUserDto
import io.transferbot.core.application.dto.command.execution.RegisterGroupChatDto
import io.transferbot.core.application.dto.command.execution.RegisterUserDto
import io.transferbot.core.application.dto.command.execution.SendMessageDto
import io.transferbot.core.application.exception.AppException
import io.transferbot.core.application.exception.EntityExistsException
import io.transferbot.core.application.exception.EntityNotFoundException
import io.transferbot.core.application.exception.TokenValidationException
import io.transferbot.core.application.port.input.*
import io.transferbot.core.application.port.output.IBotOperationsOutputPort
import io.transferbot.core.application.port.output.IGroupChatPersistenceOutputPort
import io.transferbot.core.application.port.output.IUserPersistenceOutputPort
import io.transferbot.core.application.port.output.IVerificationTokenPersistenceOutputPort
import io.transferbot.core.application.service.transfer.ITransferMessageManager
import io.transferbot.core.domain.entity.GroupChat
import io.transferbot.core.domain.entity.User
import io.transferbot.core.domain.entity.VerificationToken

class BotOperationUseCase(
    private val userPersistenceGateway: IUserPersistenceOutputPort,
    private val groupChatPersistenceGateway: IGroupChatPersistenceOutputPort,
    private val verificationTokenPersistenceGateway: IVerificationTokenPersistenceOutputPort,
    private val transferManager: ITransferMessageManager<String, TransferMessageDto>,
    private val botOperationsPresenter: IBotOperationsOutputPort
) : IBotOperationsInputPort {

    override fun sendMessage(sendMessageDto: SendMessageDto) {
        try {
            val transferId =
                userPersistenceGateway.findUserByPlatformId(sendMessageDto.senderPlatformUserId).transferId!!
            val transferMessage = sendMessageDto.let {
                TransferMessageDto(transferId, it.text, it.attachments, it.receiverChatType, it.receiverChatName)
            }
            transferManager.transfer(sendMessageDto.receiverPlatform, transferMessage)
        } catch (e: AppException) {
            botOperationsPresenter.failureSendMessage(e, sendMessageDto)
        }
    }

    override fun registerUser(registerUserDto: RegisterUserDto) {
        try {
            userPersistenceGateway.save(User(registerUserDto.platformUserId, registerUserDto.username))
            botOperationsPresenter.successRegisterUser()
        } catch (e: AppException) {
            botOperationsPresenter.failureRegisterUser(e, registerUserDto)
        }
    }

    override fun registerGroupChat(registerGroupChatDto: RegisterGroupChatDto) {
        try {
            val user = userPersistenceGateway.findUserByPlatformId(registerGroupChatDto.platformUserId)
            groupChatPersistenceGateway.save(
                GroupChat(
                    registerGroupChatDto.groupChatName,
                    registerGroupChatDto.platformGroupChatId,
                    user
                )
            )
            botOperationsPresenter.successRegisterGroupChat()
        } catch (e: AppException) {
            botOperationsPresenter.failureRegisterGroupChat(e, registerGroupChatDto)
        }
    }

    override fun createVerificationToken(platformUserId: String) {
        try {
            val user = userPersistenceGateway.findUserByPlatformId(platformUserId)
            val token = verificationTokenPersistenceGateway.save(VerificationToken(user.transferId!!))
            botOperationsPresenter.successCreateVerificationToken(token.token)
        } catch (e: AppException) {
            botOperationsPresenter.failureCreateVerificationToken(e)
        }
    }

    override fun bindUser(bindUserDto: BindUserDto) {
        try {
            val user = userPersistenceGateway.findUserByTransferName(bindUserDto.transferName)
            if (user.platformId != null) {
                throw EntityExistsException("User already registered in platform")
            }
            val token = verificationTokenPersistenceGateway.findVerificationTokenByUserTransferId(user.transferId!!)
            if (token.isTokenValid(bindUserDto.verificationToken)) {
                user.platformId = bindUserDto.platformUserId
                userPersistenceGateway.save(user)
                botOperationsPresenter.successBindUser()
            } else {
                throw TokenValidationException("Invalid token")
            }
        } catch (e: AppException) {
            botOperationsPresenter.failureBindUser(e, bindUserDto)
        }
    }
}
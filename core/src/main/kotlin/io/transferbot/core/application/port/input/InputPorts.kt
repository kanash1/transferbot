package io.transferbot.core.application.port.input

import io.transferbot.core.application.dto.TransferMessageDto
import io.transferbot.core.application.dto.command.execution.BindUserDto
import io.transferbot.core.application.dto.command.execution.RegisterGroupChatDto
import io.transferbot.core.application.dto.command.execution.RegisterUserDto
import io.transferbot.core.application.dto.command.execution.SendMessageDto

interface IBotOperationsInputPort {
    fun sendMessage(sendMessageDto: SendMessageDto)
    fun registerUser(registerUserDto: RegisterUserDto)
    fun registerGroupChat(registerGroupChatDto: RegisterGroupChatDto)
    fun createVerificationToken(platformUserId: String)
    fun bindUser(bindUserDto: BindUserDto)
}

interface ITransferMessageInputPort {
    fun transfer(transferMessageDto: TransferMessageDto)
}
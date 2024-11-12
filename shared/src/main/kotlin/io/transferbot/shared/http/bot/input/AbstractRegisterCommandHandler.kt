package io.transferbot.shared.http.bot.input

import io.transferbot.core.application.dto.ChatType
import io.transferbot.core.application.dto.command.argument.RegisterArgsDto
import io.transferbot.core.application.dto.command.execution.RegisterGroupChatDto
import io.transferbot.core.application.dto.command.execution.RegisterUserDto
import io.transferbot.core.application.exception.*
import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.core.application.service.command.parser.ICommandArgsExtractor
import io.transferbot.core.application.service.command.parser.RegisterCommandArgsExtractor
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter

abstract class AbstractRegisterCommandHandler<in Message : Any>(
    private val botOperationsUseCase: IBotOperationsInputPort,
    private val commandHandlerPresenter: CommandHandlerPresenter,
    private val argsExtractor: ICommandArgsExtractor<RegisterArgsDto> = RegisterCommandArgsExtractor()
) : ICommandHandler<Message> {

    override fun handle(args: String, message: Message) {
        try {
            val chatType = getChatType(message)
            val commandArgs = argsExtractor.extract(args)

            when (chatType) {
                ChatType.USER -> botOperationsUseCase.registerUser(mapToRegisterUserData(commandArgs, message))
                ChatType.GROUP -> botOperationsUseCase.registerGroupChat(mapToRegisterGroupChatData(commandArgs, message))
            }
        } catch (e: ParseException) {
            commandHandlerPresenter.failureParse()
        }
    }

    protected abstract fun getChatType(message: Message): ChatType

    protected abstract fun mapToRegisterUserData(commandArgs: RegisterArgsDto, message: Message): RegisterUserDto

    protected abstract fun mapToRegisterGroupChatData(
        commandArgs: RegisterArgsDto,
        message: Message
    ): RegisterGroupChatDto
}
package io.transferbot.shared.http.bot.input

import io.transferbot.core.application.dto.ChatType
import io.transferbot.core.application.dto.command.argument.BindArgsDto
import io.transferbot.core.application.dto.command.execution.BindUserDto
import io.transferbot.core.application.exception.ParseException
import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.core.application.service.command.parser.BindCommandArgsExtractor
import io.transferbot.core.application.service.command.parser.ICommandArgsExtractor
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter

abstract class AbstractBindCommandHandler<in Message: Any>(
    private val botOperationsUseCase: IBotOperationsInputPort,
    private val commandHandlerPresenter: CommandHandlerPresenter,
    private val argsExtractor: ICommandArgsExtractor<BindArgsDto> = BindCommandArgsExtractor()
) : ICommandHandler<Message> {

    override fun handle(args: String, message: Message) {
        val chatType = getChatType(message)
        if (chatType == ChatType.USER) {
            try {
                val commandArgs = argsExtractor.extract(args)
                val dto = mapToBindData(commandArgs, message)
                botOperationsUseCase.bindUser(dto)
            } catch (e: ParseException) {
                commandHandlerPresenter.failureParse()
            }
        } else {
            commandHandlerPresenter.failureChatType(chatType)
        }
    }

    protected abstract fun mapToBindData(bindArgs: BindArgsDto, message: Message): BindUserDto

    protected abstract fun getChatType(message: Message): ChatType
}
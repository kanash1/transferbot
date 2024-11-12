package io.transferbot.shared.http.bot.input

import io.transferbot.core.application.dto.command.argument.SendArgsDto
import io.transferbot.core.application.dto.command.execution.SendMessageDto
import io.transferbot.core.application.exception.ParseException
import io.transferbot.core.application.port.input.IBotOperationsInputPort
import io.transferbot.core.application.service.command.parser.ICommandArgsExtractor
import io.transferbot.shared.http.bot.output.CommandHandlerPresenter

abstract class AbstractSendCommandHandler<in Message: Any>(
    private val botOperationsUseCase: IBotOperationsInputPort,
    private val commandHandlerPresenter: CommandHandlerPresenter,
    private val argsExtractor: ICommandArgsExtractor<SendArgsDto>
) : ICommandHandler<Message> {

    override fun handle(args: String, message: Message) {
        try {
            val sendArgs = argsExtractor.extract(args)
            val dto = mapToSendData(sendArgs, message)
            botOperationsUseCase.sendMessage(dto)
        } catch (e: ParseException) {
            commandHandlerPresenter.failureParse()
        }
    }

    protected abstract fun mapToSendData(sendArgs: SendArgsDto, message: Message): SendMessageDto
}
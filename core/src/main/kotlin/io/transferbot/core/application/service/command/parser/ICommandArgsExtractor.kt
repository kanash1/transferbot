package io.transferbot.core.application.service.command.parser

interface ICommandArgsExtractor<out CommandArgs: Any> {
    fun extract(args: String): CommandArgs
}
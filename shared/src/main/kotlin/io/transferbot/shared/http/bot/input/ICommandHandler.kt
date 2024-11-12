package io.transferbot.shared.http.bot.input

interface ICommandHandler<in Message: Any> {
    fun handle(args: String, message: Message)
}
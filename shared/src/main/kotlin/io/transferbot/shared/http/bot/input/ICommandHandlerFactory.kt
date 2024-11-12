package io.transferbot.shared.http.bot.input

interface ICommandHandlerFactory<Message: Any> {
    val command: String
    fun createHandler(chatId: String): ICommandHandler<Message>
}
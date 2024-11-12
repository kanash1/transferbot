package io.transferbot.telegram.exception

class TelegramApiException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)
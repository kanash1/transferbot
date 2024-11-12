package io.transferbot.shared.exception

class UnsupportedMediaException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class UnsupportedChatTypeException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)
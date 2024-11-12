package io.transferbot.core.application.exception

sealed class AppException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

sealed class PersistenceException(message: String? = null, cause: Throwable? = null) : AppException(message, cause)

class EntityExistsException(message: String? = null, cause: Throwable? = null) : PersistenceException(message, cause)

class EntityNotFoundException(message: String? = null, cause: Throwable? = null) : PersistenceException(message, cause)

class TokenValidationException(message: String? = null, cause: Throwable? = null) : AppException(message, cause)

class ObjectBuildException(message: String? = null, cause: Throwable? = null) : AppException(message, cause)

class ParseException(message: String? = null, cause: Throwable? = null) : AppException(message, cause)

class UnknownValueException(message: String? = null, cause: Throwable? = null) : AppException(message, cause)

class TransferException(message: String? = null, cause: Throwable? = null) : AppException(message, cause)
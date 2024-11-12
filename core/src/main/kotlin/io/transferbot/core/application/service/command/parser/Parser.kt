package io.transferbot.core.application.service.command.parser

import io.transferbot.core.application.exception.ParseException

fun splitTextIntoCommandAndArgs(text: String): Pair<String, String> {
    val trimmedText = text.trim()
    val command: String = trimmedText.substringBefore(' ').let {
        if (!it.contains('/'))
            throw ParseException("Command not found in text: $text")
        it.substringAfter('/').lowercase()
    }
    val args = trimmedText.substringAfter(' ', "")
    return command to args
}
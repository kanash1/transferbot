package io.transferbot.core.application.service.command.parser.argument.extractor

interface IArgExtractor<out T> {
    fun extract(pipeline: String): Pair<T, String>
}
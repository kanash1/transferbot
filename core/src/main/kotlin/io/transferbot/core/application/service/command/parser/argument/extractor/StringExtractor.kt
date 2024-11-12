package io.transferbot.core.application.service.command.parser.argument.extractor

import io.transferbot.core.application.exception.ParseException

class StringExtractor : IArgExtractor<String> {

    override fun extract(pipeline: String): Pair<String, String> {
        val list = pipeline.trim().split(" ", limit = 2)
        val arg = list.getOrNull(0)
        if (arg.isNullOrEmpty())
            throw ParseException("Empty string argument")
        val tail = list.getOrElse(1) { "" }
        return Pair(arg, tail)
    }
}
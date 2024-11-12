package io.transferbot.core.application.service.command.parser.argument.extractor

import io.transferbot.core.application.exception.ParseException

class RegexExtractor<out T: Any>(
    private val regex: Regex,
    private val toArg: (String) -> T
): IArgExtractor<T> {

    constructor(regex: String, caster: (String) -> T) : this(regex.toRegex(), caster)

    override fun extract(pipeline: String): Pair<T, String> {
        val (arg, tail) = StringExtractor().extract(pipeline)
        if (regex.matches(arg))
            return toArg(arg) to tail
        throw ParseException("Argument $arg does not meet the requirements: ${regex.pattern}")
    }
}
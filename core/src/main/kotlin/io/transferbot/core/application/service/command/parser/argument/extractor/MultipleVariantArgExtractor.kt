package io.transferbot.core.application.service.command.parser.argument.extractor

import io.transferbot.core.application.exception.ParseException

class MultipleVariantArgExtractor<out T: Any>(
    private val extractorVariants: Set<IArgExtractor<T>>
): IArgExtractor<T> {

    override fun extract(pipeline: String): Pair<T, String> {
        for (extractor in extractorVariants) {
            try {
                return extractor.extract(pipeline)
            } catch (ignore: Exception) {}
        }
        throw ParseException("There is no variant of extractor for pipeline: $pipeline")
    }
}
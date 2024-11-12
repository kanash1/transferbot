package io.transferbot.core.application.service.command.parser.argument.extractor

import io.transferbot.core.application.exception.ParseException

class MultipleNamingArgExtractor<out T: Any>(
    namingVariants: Set<String>,
    private val arg: T,
    private val ignoreVariantCase: Boolean = true
): IArgExtractor<T> {
    private val variants: Set<String> =
        if (ignoreVariantCase) namingVariants.map { it.lowercase() }.toSet() else namingVariants

    override fun extract(pipeline: String): Pair<T, String> {
        var (variant, tail) = StringExtractor().extract(pipeline)
        if (ignoreVariantCase)
            variant = variant.lowercase()
        if (variants.contains(variant))
            return arg to tail
        throw ParseException("There is no variant of argument $arg named $variant")
    }
}
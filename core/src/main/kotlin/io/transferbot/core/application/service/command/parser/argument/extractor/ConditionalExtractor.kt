package io.transferbot.core.application.service.command.parser.argument.extractor

class ConditionalExtractor<out T>(
    private val extractor: IArgExtractor<T>,
    private val standardValue: T,
    private val canExtract: () -> Boolean,
): IArgExtractor<T> {
    override fun extract(pipeline: String): Pair<T, String> {
        return if (canExtract())
            extractor.extract(pipeline)
        else
            Pair(standardValue, pipeline)
    }
}
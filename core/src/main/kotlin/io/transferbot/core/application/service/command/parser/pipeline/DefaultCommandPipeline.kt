package io.transferbot.core.application.service.command.parser.pipeline

import io.transferbot.core.application.service.command.parser.argument.extractor.IArgExtractor

class DefaultCommandPipeline<CommandArgs> : ICommandPipeline<CommandArgs> {
    private val extractorsAndSetters: MutableList<Pair<IArgExtractor<Any?>, (Any?) -> Unit>> = mutableListOf()

    @Suppress("UNCHECKED_CAST")
    override fun <T> addExtractorAndSetter(
        extractor: IArgExtractor<T>,
        argSetter: (arg: T) -> Unit
    ): ICommandPipeline<CommandArgs> {
        extractorsAndSetters.add(extractor to (argSetter as (Any?) -> Unit))
        return this
    }

    override fun extract(args: String) {
        var pipeline = args
        for ((extractor, argSetter) in extractorsAndSetters) {
            val (arg, newPipeline) = extractor.extract(pipeline)
            argSetter(arg)
            pipeline = newPipeline
        }
    }
}
package io.transferbot.core.application.service.command.parser.pipeline

import io.transferbot.core.application.service.command.parser.argument.extractor.IArgExtractor

interface ICommandPipeline<CommandArgs> {
    fun <T> addExtractorAndSetter(extractor: IArgExtractor<T>, argSetter: (T) -> Unit): ICommandPipeline<CommandArgs>
    fun extract(args: String)
}
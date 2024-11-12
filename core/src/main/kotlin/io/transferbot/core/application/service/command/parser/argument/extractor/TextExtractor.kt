package io.transferbot.core.application.service.command.parser.argument.extractor

class TextExtractor: IArgExtractor<String> {
    override fun extract(pipeline: String): Pair<String, String> {
        return pipeline to ""
    }
}
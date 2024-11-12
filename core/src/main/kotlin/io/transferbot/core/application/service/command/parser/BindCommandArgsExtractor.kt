package io.transferbot.core.application.service.command.parser

import io.transferbot.core.application.service.command.parser.argument.extractor.StringExtractor
import io.transferbot.core.application.service.command.parser.pipeline.DefaultCommandPipeline
import io.transferbot.core.application.dto.command.argument.BindArgsDto

class BindCommandArgsExtractor: ICommandArgsExtractor<BindArgsDto> {

    override fun extract(args: String): BindArgsDto {
        val commandPipeline = DefaultCommandPipeline<BindArgsDto>()
        val builder = BindArgsDto.Builder()
        commandPipeline
            .addExtractorAndSetter(StringExtractor(), builder::username)
            .addExtractorAndSetter(StringExtractor(), builder::verificationCode)
            .extract(args)
        return builder.build()
    }
}
package io.transferbot.core.application.service.command.parser

import io.transferbot.core.application.service.command.parser.pipeline.DefaultCommandPipeline
import io.transferbot.core.application.dto.command.argument.RegisterArgsDto
import io.transferbot.core.application.service.command.parser.argument.extractor.RegexExtractor

class RegisterCommandArgsExtractor : ICommandArgsExtractor<RegisterArgsDto> {

    override fun extract(args: String): RegisterArgsDto {
        val commandPipeline = DefaultCommandPipeline<RegisterArgsDto>()
        val builder = RegisterArgsDto.Builder()
        commandPipeline
            .addExtractorAndSetter(
                RegexExtractor("^(?=.{4,16}\$)(?![_0-9])[a-zA-Z0-9_]+(?<![_])\$") { it },
                builder::chatName
            )
            .extract(args)
        return builder.build()
    }
}
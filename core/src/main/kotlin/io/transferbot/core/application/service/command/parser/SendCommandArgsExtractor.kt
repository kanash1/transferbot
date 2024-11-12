package io.transferbot.core.application.service.command.parser

import io.transferbot.core.application.service.command.parser.argument.extractor.*
import io.transferbot.core.application.service.command.parser.pipeline.DefaultCommandPipeline
import io.transferbot.core.application.dto.ChatType
import io.transferbot.core.application.dto.command.argument.SendArgsDto
import io.transferbot.core.application.exception.ParseException

class SendCommandArgsExtractor(
    platformToNames: Map<String, Set<String>>
) : ICommandArgsExtractor<SendArgsDto> {

    private val platformArgExtractor = MultipleVariantArgExtractor(
        platformToNames.map {
            MultipleNamingArgExtractor(it.value, it.key)
        }.toHashSet()
    )
    private val chatTypeArgExtractor = MultipleVariantArgExtractor(
        hashSetOf(
            MultipleNamingArgExtractor(hashSetOf("g", "group"), ChatType.GROUP),
            MultipleNamingArgExtractor(hashSetOf("u", "user"), ChatType.USER)
        )
    )

    override fun extract(args: String): SendArgsDto {
        try {
            val commandPipeline = DefaultCommandPipeline<SendArgsDto>()
            val builder = SendArgsDto.Builder()
            commandPipeline
                .addExtractorAndSetter(platformArgExtractor, builder::platform)
                .addExtractorAndSetter(chatTypeArgExtractor, builder::chatType)
                .addExtractorAndSetter(
                    ConditionalExtractor(
                        StringExtractor(),
                        null
                    ) { builder.chatType() == ChatType.GROUP }, builder::chatName
                )
                .addExtractorAndSetter(TextExtractor(), builder::text)
                .extract(args)
            return builder.build()
        } catch (e: ParseException) {
            throw ParseException("Invalid arguments")
        }
    }
}
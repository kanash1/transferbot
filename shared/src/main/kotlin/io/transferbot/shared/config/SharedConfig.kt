package io.transferbot.shared.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class SharedConfig {
    @Bean
    fun objectMapper() = jacksonObjectMapper()

    @Bean
    fun webClient(): WebClient = WebClient.create()
}
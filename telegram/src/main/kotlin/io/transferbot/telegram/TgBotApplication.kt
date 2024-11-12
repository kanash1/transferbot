package io.transferbot.telegram

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["io.transferbot.shared", "io.transferbot.tg"])
@EntityScan(basePackages = ["io.transferbot.shared", "io.transferbot.tg"])
@EnableJpaRepositories(basePackages = ["io.transferbot.shared", "io.transferbot.tg"])
class TgBotApplication

fun main(args: Array<String>) {
    runApplication<TgBotApplication>(*args)
}
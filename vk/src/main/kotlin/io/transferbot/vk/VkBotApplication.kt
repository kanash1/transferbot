package io.transferbot.vk

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["io.transferbot.shared", "io.transferbot.vk"])
@EntityScan(basePackages = ["io.transferbot.shared", "io.transferbot.vk"])
@EnableJpaRepositories(basePackages = ["io.transferbot.shared", "io.transferbot.vk"])
class VkBotApplication

fun main(args: Array<String>) {
	runApplication<VkBotApplication>(*args)
}

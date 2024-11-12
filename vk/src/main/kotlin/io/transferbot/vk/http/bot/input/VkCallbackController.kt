package io.transferbot.vk.http.bot.input

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/vk/callback")
class VkCallbackController(private val vkCallbackApi: VkCallbackApi) {
    private val log = KotlinLogging.logger {  }

    @PostMapping
    fun acceptEvent(@RequestBody eventJson: String): String {
        log.debug { "Received request: $eventJson" }
        return vkCallbackApi.parseMessage(eventJson)
    }
}
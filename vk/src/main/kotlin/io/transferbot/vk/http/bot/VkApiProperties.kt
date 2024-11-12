package io.transferbot.vk.http.bot

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties("vk-api")
data class VkApiProperties(
    @field:NotNull val groupId: Long?,
    @field:NotEmpty val accessToken: String,
//    @field:NotEmpty val confirmationKey: String,
    val secretKey: String? = null
)

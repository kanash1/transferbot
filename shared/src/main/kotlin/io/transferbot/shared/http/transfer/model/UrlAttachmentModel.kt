package io.transferbot.shared.http.transfer.model

import io.transferbot.core.application.dto.AttachmentDto

data class UrlAttachmentModel(
    val type: String,
    val url: String,
    val name: String? = null
)

fun AttachmentDto.toModel() = UrlAttachmentModel(type.name.lowercase(), url!!, name)

fun UrlAttachmentModel.toDto() = AttachmentDto(
    enumValueOf(type.uppercase()),
    url,
    null,
    name
)
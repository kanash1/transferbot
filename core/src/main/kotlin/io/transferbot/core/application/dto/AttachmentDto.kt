package io.transferbot.core.application.dto

data class AttachmentDto(
    val type: AttachmentType,
    val url: String? = null,
    val file: ByteArray? = null,
    val name: String? = null
)

enum class AttachmentType {
    PHOTO,
    FILE
}

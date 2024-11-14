package io.transferbot.shared.util

import io.transferbot.core.application.dto.AttachmentDto
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels

fun File.writeFromAttachment(attachmentDto: AttachmentDto) {
    if (attachmentDto.url != null) {
        val fileChannel = FileOutputStream(this).channel
        fileChannel.transferFrom(Channels.newChannel(URL(attachmentDto.url).openStream()), 0, Long.MAX_VALUE)
    } else if (attachmentDto.file != null) {
        this.writeBytes(attachmentDto.file!!)
    } else {
        throw IllegalArgumentException("Attachment URL and file is null")
    }
}
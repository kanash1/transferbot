package io.transferbot.shared.util

import io.transferbot.core.application.dto.AttachmentDto
import io.transferbot.core.application.dto.AttachmentType
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import kotlin.io.path.createTempFile

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

fun AttachmentDto.toFile(): File {
    when (type) {
        AttachmentType.PHOTO -> {
            val filePath = createTempFile(suffix = ".jpg")
            val file = filePath.toFile()
            file.writeFromAttachment(this)
            return file
        }
        AttachmentType.FILE -> {
            val docNameLen = name?.length ?: 0
            val lastDotIdx = name?.lastIndexOf('.') ?: docNameLen
            val filePath = createTempFile(suffix = name?.substring(lastDotIdx, docNameLen))
            val file = filePath.toFile()
            file.writeFromAttachment(this)
            return file
        }
    }
}
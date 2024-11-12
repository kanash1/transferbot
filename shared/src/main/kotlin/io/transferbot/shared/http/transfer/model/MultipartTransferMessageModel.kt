package io.transferbot.shared.http.transfer.model

import com.fasterxml.jackson.databind.ObjectMapper
import io.transferbot.core.application.dto.AttachmentDto
import io.transferbot.core.application.dto.AttachmentType
import io.transferbot.core.application.dto.TransferMessageDto
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile

data class MultipartTransferMessageModel(
    val message: String,
    val file0: MultipartFile? = null,
    val file1: MultipartFile? = null,
    val file2: MultipartFile? = null,
    val file3: MultipartFile? = null,
    val file4: MultipartFile? = null,
    val file5: MultipartFile? = null,
    val file6: MultipartFile? = null,
    val file7: MultipartFile? = null,
    val file8: MultipartFile? = null,
    val file9: MultipartFile? = null
)

data class MultipartTransferMessage(
    val userTransferId: Long,
    val text: String,
    val chatType: String,
    val chatName: String?
)

fun MultipartFile.toAttachmentDto() = AttachmentDto(
    if (MediaType.IMAGE_JPEG_VALUE == contentType) AttachmentType.PHOTO else AttachmentType.FILE,
    null,
    bytes,
    originalFilename
)

fun MultipartTransferMessageModel.toDto(objectMapper: ObjectMapper): TransferMessageDto {
    val transferMessageRequestModel = objectMapper.readValue(message, MultipartTransferMessage::class.java)

    return TransferMessageDto(
        transferMessageRequestModel.userTransferId,
        transferMessageRequestModel.text,
        mutableListOf<AttachmentDto>().apply {
            if (file0 != null) add(file0.toAttachmentDto())
            if (file1 != null) add(file1.toAttachmentDto())
            if (file2 != null) add(file2.toAttachmentDto())
            if (file3 != null) add(file3.toAttachmentDto())
            if (file4 != null) add(file4.toAttachmentDto())
            if (file5 != null) add(file5.toAttachmentDto())
            if (file6 != null) add(file6.toAttachmentDto())
            if (file7 != null) add(file7.toAttachmentDto())
            if (file8 != null) add(file8.toAttachmentDto())
            if (file9 != null) add(file9.toAttachmentDto())
        },
        enumValueOf(transferMessageRequestModel.chatType.uppercase()),
        transferMessageRequestModel.chatName
    )
}
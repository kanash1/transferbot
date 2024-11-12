package io.transferbot.vk.http.bot.output

import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.GroupActor
import com.vk.api.sdk.exceptions.ApiException
import com.vk.api.sdk.objects.docs.responses.DocUploadResponse
import com.vk.api.sdk.objects.photos.responses.PhotoUploadResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import io.transferbot.core.application.dto.AttachmentDto
import io.transferbot.core.application.dto.AttachmentType
import io.transferbot.core.application.dto.ChatType
import io.transferbot.vk.exception.VkApiException
import io.transferbot.vk.http.bot.VkApiProperties
import io.transferbot.vk.http.bot.model.VkCallbackMessageModel
import io.transferbot.vk.http.bot.model.VkPostMessageModel
import io.transferbot.vk.http.bot.output.IVkService.Companion.GROUP_CHAT_PEER_ID_LOWER_BOUND
import io.transferbot.vk.http.bot.output.IVkService.Companion.getChatType
import io.transferbot.vk.http.toModel
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import kotlin.io.path.deleteIfExists
import kotlin.io.path.createTempFile
import kotlin.random.Random

@Service
class VkService(
    private val vk: VkApiClient,
    props: VkApiProperties
) : IVkService {
    private val log = KotlinLogging.logger { }
    private val groupActor = GroupActor(props.groupId, props.accessToken)

    override fun findMessageByIdInUserChat(id: Long): VkCallbackMessageModel {
        try {
            val response = vk.messages()
                .getById(groupActor)
                .messageIds(id.toInt())
                .execute()
            return response.items.getOrNull(0)!!.toModel()
        } catch (e: ApiException) {
            log.error { e.message }
            throw VkApiException(e.message, e)
        } catch (e: Exception) {
            log.error { e.message }
            throw VkApiException(e.message, e)
        }
    }

    override fun findMessageByIdInGroupChat(peerId: Long, conversationMessageId: Int): VkCallbackMessageModel {
        try {
            val response = vk.messages()
                .getByConversationMessageId(groupActor)
                .conversationMessageIds(conversationMessageId)
                .peerId(peerId)
                .execute()
            return response.items.getOrNull(0)!!.toModel()
        } catch (e: ApiException) {
            log.error { e.message }
            throw VkApiException(e.message, e)
        } catch (e: Exception) {
            log.error { e.message }
            throw VkApiException(e.message, e)
        }
    }

    override fun postMessage(vkPostMessageModel: VkPostMessageModel) {
        try {
            val attachmentsRow = vkPostMessageModel.run { createAttachmentsRow(attachments, peerId) }

            vk.messages().sendDeprecated(groupActor)
                .apply {
                    vkPostMessageModel.run {
                        if (getChatType(peerId) == ChatType.USER) userId(peerId)
                        else chatId(peerId.toInt() - GROUP_CHAT_PEER_ID_LOWER_BOUND)
                    }
                }
                .attachment(attachmentsRow)
                .randomId(Random.nextInt())
                .message(vkPostMessageModel.text)
                .execute()
        } catch (e: ApiException) {
            log.error { e.message }
            throw VkApiException(e.message, e)
        }  catch (e: Exception) {
            log.error { e.message }
            throw VkApiException(e.message, e)
        }
    }

    override fun getCallbackConfirmationCode(): String =
        vk.groups().getCallbackConfirmationCode(groupActor).execute().code!!

    private fun createAttachmentsRow(attachments: List<AttachmentDto>, vkId: Long): String {
        val attachmentsRowBuilder = StringBuilder()

        val fileWriter: File.(attachment: AttachmentDto) -> Unit =
            if (attachments.isNotEmpty() && attachments.first().url != null) {
                { this.writeFromUrl(it.url!!) }
            } else {
                { this.writeBytes(it.file!!) }
            }

        for ((idx, attachment) in attachments.withIndex()) {
            when (attachment.type) {
                AttachmentType.PHOTO -> {
                    val res = saveMessagesPhoto(uploadPhoto(attachment, fileWriter))
                    if (!res.isNullOrEmpty()) {
                        attachmentsRowBuilder.append("photo${res[0].ownerId}_${res[0].id}")
                        if (idx != attachments.lastIndex)
                            attachmentsRowBuilder.append(',')
                    }
                }

                AttachmentType.FILE -> {
                    val res = saveDoc(uploadDoc(attachment, vkId.toInt(), fileWriter))
                    attachmentsRowBuilder.append("doc${res.doc.ownerId}_${res.doc.id}")
                    if (idx != attachments.lastIndex)
                        attachmentsRowBuilder.append(',')
                }
            }
        }

        return attachmentsRowBuilder.toString()
    }

    private fun File.writeFromUrl(url: String) {
        val fileChannel = FileOutputStream(this).channel
        fileChannel.transferFrom(Channels.newChannel(URL(url).openStream()), 0, Long.MAX_VALUE)
    }

    private fun getPhotosUploadServer() = vk.photos().getMessagesUploadServer(groupActor).execute().uploadUrl

    private fun uploadPhoto(
        photo: AttachmentDto,
        fileWriter: (file: File, attachment: AttachmentDto) -> Unit
    ): PhotoUploadResponse {
        val filePath = createTempFile(suffix = ".jpg")
        val file = filePath.toFile()
        fileWriter(file, photo)
        val response = trySeveralTimes { vk.upload().photo(getPhotosUploadServer().toString(), file).execute() }
        filePath.deleteIfExists()
        return response
    }

    private fun saveMessagesPhoto(photoData: PhotoUploadResponse) =
        vk.photos().saveMessagesPhoto(groupActor)
            .photo(photoData.photo)
            .server(photoData.server)
            .hash(photoData.hash)
            .execute()

    private fun getDocsUploadServer(peerId: Int) =
        vk.docs().getMessagesUploadServer(groupActor).peerId(peerId).execute().uploadUrl

    private fun uploadDoc(
        doc: AttachmentDto,
        peerId: Int,
        fileWriter: (file: File, attachment: AttachmentDto) -> Unit
    ): DocUploadResponse {
        val docNameLen = doc.name?.length ?: 0
        val lastDotIdx = doc.name?.lastIndexOf('.') ?: docNameLen
        val tmpFilePath = createTempFile(suffix = doc.name?.substring(lastDotIdx, docNameLen))
        val tmpFile = tmpFilePath.toFile()
        fileWriter(tmpFile, doc)
        val response = trySeveralTimes { vk.upload().doc(getDocsUploadServer(peerId).toString(), tmpFile).execute() }
        tmpFilePath.deleteIfExists()
        return response
    }

    private fun saveDoc(docData: DocUploadResponse) =
        vk.docs().save(groupActor)
            .file(docData.file)
            .execute()

    private fun <T> trySeveralTimes(repeatsCount: Byte = 5, func: () -> T): T {
        var counter: Byte = 0
        var exception: Exception? = null
        while (counter != repeatsCount) {
            try {
                return func()
            } catch (e: Exception) {
                ++counter
                exception = e
            }
        }
        throw exception!!
    }
}
package io.transferbot.shared.http.transfer.output

import io.transferbot.core.application.port.output.ITransferMessageOutputPort
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.server.DelegatingServerHttpResponse
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.stereotype.Service

@Service
class HttpTransferMessagePresenter(
    private val jacksonConverter: MappingJackson2HttpMessageConverter,
    httpServletResponse: HttpServletResponse,
): ITransferMessageOutputPort {
    private val httpOutputMessage = DelegatingServerHttpResponse(ServletServerHttpResponse(httpServletResponse))

    override fun success() {
        httpOutputMessage.setStatusCode(HttpStatus.OK)
        jacksonConverter.write("Success", MediaType.APPLICATION_JSON, httpOutputMessage)
    }

    override fun failure(errorMessage: String) {
        httpOutputMessage.setStatusCode(HttpStatus.BAD_REQUEST)
        jacksonConverter.write(errorMessage, MediaType.APPLICATION_JSON, httpOutputMessage)
    }
}
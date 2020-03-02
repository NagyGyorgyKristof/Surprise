package hu.ngykristof.surprise.commonscore.interceptor

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class HeaderForwarderInterceptor(
        private val headerName: String
) : RequestInterceptor {

    override fun apply(requestTemplate: RequestTemplate) {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        val header = request.getHeader(headerName)
        if (header != null && header.isNotEmpty()) {
            requestTemplate.header(headerName, header)
        }
    }
}
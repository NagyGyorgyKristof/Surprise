package hu.ngykristof.surprise.gateway.extensions

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


fun ServerWebExchange.getTokenWithHeadersCheck(): String {
    val authHeaders = this.request.headers.getFirst(HttpHeaders.AUTHORIZATION)

    if (authHeaders == null || authHeaders.isEmpty()) {
        sendUnauthorisedError()
    }
    if (authHeaders?.contains("Bearer".toRegex()) == false) {
        sendUnauthorisedError()
    }

    return authHeaders ?: ""
}


fun ServerWebExchange.sendUnauthorisedError(): Mono<Void> {
    val response = this.response
    response.statusCode = HttpStatus.UNAUTHORIZED
    response.headers.contentType = MediaType.APPLICATION_JSON
    val error = Mono.just(response.bufferFactory().allocateBuffer())
    return response.writeWith(error)
}


fun ServerWebExchange.putAccessTokenOnRequest(accessToken: String, chain: GatewayFilterChain): Mono<Void> {
    this.request
            .mutate()
            .header(HttpHeaders.AUTHORIZATION, accessToken)

    return chain.filter(this)
}
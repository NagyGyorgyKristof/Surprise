package hu.ngykristof.surprise.gateway.extensions

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.core.ResolvableType
import org.springframework.core.codec.Hints
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


fun ServerWebExchange.getTokenWithHeadersCheck(): String {
    val authHeaders = this.request.headers.getFirst(HttpHeaders.AUTHORIZATION)

    if (authHeaders == null || authHeaders.isEmpty()) {
        sendUnauthorisedError("Authorization header is null or empty")
    }
    if (authHeaders?.contains("Bearer".toRegex()) == false) {
        sendUnauthorisedError("The header does not contain Bearer prefix!")
    }

    return authHeaders ?: ""
}


fun ServerWebExchange.sendUnauthorisedError(errorMessage: String = ""): Mono<Void> {
    this.response.statusCode = HttpStatus.UNAUTHORIZED
    return writeJsonBody(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage))
}


fun ServerWebExchange.putAccessTokenOnRequest(accessToken: String, chain: GatewayFilterChain): Mono<Void> {
    this.request
            .mutate()
            .header(HttpHeaders.AUTHORIZATION, accessToken)

    return chain.filter(this)
}

fun ServerWebExchange.writeJsonBody(body: Any) =
        this.response.writeWith(
                Jackson2JsonEncoder().encode(
                        Mono.just(body),
                        this.response.bufferFactory(),
                        ResolvableType.forInstance(body),
                        MediaType.APPLICATION_JSON,
                        Hints.from(Hints.LOG_PREFIX_HINT, this.logPrefix))
        )

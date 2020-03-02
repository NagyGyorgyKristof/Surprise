package hu.ngykristof.surprise.gateway.filter

import hu.ngykristof.surprise.authapi.AuthFeignClient
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.ValidateTokenRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.ValidateTokenResponse
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

@Component
class AuthenticatedRequestFilter(
        private val authFeignClient: AuthFeignClient
) : GatewayFilterFactory<Any> {

    companion object {
        const val jwtHeader: String = "Authorization"
    }


    override fun apply(config: Any): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val httpRequest = exchange.request
            getAccessToken(httpRequest)
                    .flatMap { accessToken -> checkAccessToken(accessToken) }
                    .flatMap { accessToken -> putJwtOnRequest(httpRequest, accessToken) }
                    .flatMap { request -> chain.filter(exchange.mutate().request(request).build()) }
        }
    }

    private fun getAccessToken(request: ServerHttpRequest): Mono<String> {
        return Mono.justOrEmpty(Optional.ofNullable(request.headers.getFirst(jwtHeader)))
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED)))
    }

    private fun checkAccessToken(accessToken: String): Mono<String> {
        val request = ValidateTokenRequest(accessToken = accessToken)
        var response: Mono<ValidateTokenResponse> = Mono.fromCallable { authFeignClient.validateToken(request) }
        response = response.subscribeOn(Schedulers.elastic())
        return response
                .filter { it.isValid }
                .map { accessToken }
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED)));
    }

    private fun putJwtOnRequest(request: ServerHttpRequest, accessToken: String): Mono<ServerHttpRequest> {
        return Mono.justOrEmpty(Optional.ofNullable(request.mutate().header(jwtHeader, accessToken).build()))
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED)))
    }
}




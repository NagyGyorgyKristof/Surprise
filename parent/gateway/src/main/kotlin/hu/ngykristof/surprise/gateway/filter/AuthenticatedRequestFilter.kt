package hu.ngykristof.surprise.gateway.filter

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationResponse
import hu.ngykristof.surprise.gateway.filter.AuthenticatedRequestFilter.ValidationResponseParseResult.Failure
import hu.ngykristof.surprise.gateway.filter.AuthenticatedRequestFilter.ValidationResponseParseResult.Success
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@Component
class AuthenticatedRequestFilter(
        private val webClientBuilder: WebClient.Builder,
        @Value("\${surprise.validate-access-token-url}")
        private val accessTokenUrl: String,
        private val mapper: ObjectMapper
) : GatewayFilterFactory<Any> {

    override fun newConfig(): Any {
        return Any()
    }

    override fun apply(config: Any): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val httpRequest = exchange.request
            getAccessToken(httpRequest)
                    .flatMap { accessToken -> checkAccessToken(accessToken) }
                    .flatMap { response -> putTokenOnRequest(response, exchange, chain) }
        }
    }

    private fun getAccessToken(request: ServerHttpRequest): Mono<String> {
        return Mono.justOrEmpty(Optional.ofNullable(request.headers.getFirst(HttpHeaders.AUTHORIZATION)))
                .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED)))
    }

    private fun checkAccessToken(accessToken: String): Mono<ClientResponse> {
        return webClientBuilder.build()
                .post()
                .uri(accessTokenUrl)
                .bodyValue(TokenValidationRequest(accessToken = accessToken))
                .exchange()
    }

    private fun putTokenOnRequest(
            tokenValidationClientResponse: ClientResponse,
            exchange: ServerWebExchange,
            chain: GatewayFilterChain
    ): Mono<Void> {
        return tokenValidationClientResponse.bodyToMono(String::class.java)
                .flatMap { responseBody ->

                    when (val parseResult = tryToParseValidationResponse(responseBody)) {
                        is Success -> {
                            if (isAccessTokenValid(parseResult)) {
                                exchange.request
                                        .mutate()
                                        .header(
                                                HttpHeaders.AUTHORIZATION,
                                                parseResult.tokenValidationResponse.accessToken
                                        )

                                chain.filter(exchange)
                            } else {
                                sendUnauthorisedError(exchange)
                            }
                        }
                        is Failure -> {
                            sendUnauthorisedError(exchange)
                        }
                    }
                }
    }

    private fun tryToParseValidationResponse(responseBody: String): ValidationResponseParseResult {
        return try {
            Success(mapper.readValue(responseBody, TokenValidationResponse::class.java))
        } catch (e: JsonProcessingException) {
            Failure
        }
    }

    private fun sendUnauthorisedError(exchange: ServerWebExchange): Mono<Void?>? {
        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON
        val error = Mono.just(response.bufferFactory().allocateBuffer())
        return response.writeWith(error)
    }

    private fun isAccessTokenValid(result: Success) =
            result.tokenValidationResponse.isValid

    sealed class ValidationResponseParseResult {
        data class Success(val tokenValidationResponse: TokenValidationResponse) : ValidationResponseParseResult()
        object Failure : ValidationResponseParseResult()

    }
}




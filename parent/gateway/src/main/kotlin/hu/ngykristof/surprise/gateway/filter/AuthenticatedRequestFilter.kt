package hu.ngykristof.surprise.gateway.filter

import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationRequest
import hu.ngykristof.surprise.authapi.dto.tokenvalidation.TokenValidationResponse
import hu.ngykristof.surprise.gateway.extensions.getTokenWithHeadersCheck
import hu.ngykristof.surprise.gateway.extensions.putAccessTokenOnRequest
import hu.ngykristof.surprise.gateway.extensions.sendUnauthorisedError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import org.springframework.web.server.ServerWebExchange

@Component
class AuthenticatedRequestFilter(
        private val webClientBuilder: WebClient.Builder,
        @Value("\${surprise.validate-access-token-url}")
        private val validateAccessTokenUrl: String
) : GatewayFilterFactory<Any> {

    override fun newConfig(): Any {
        return Any()
    }

    override fun apply(config: Any): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val accessToken = exchange.getTokenWithHeadersCheck()
            val tokenValidationResponse = checkAccessToken(accessToken)

            if (tokenValidationResponse.isValid) {
                exchange.putAccessTokenOnRequest(accessToken, chain)
            } else {
                exchange.sendUnauthorisedError(tokenValidationResponse.errorMessage ?: "")
            }
        }
    }

    private fun checkAccessToken(accessToken: String): TokenValidationResponse = runBlocking(Dispatchers.Default) {
        executeTokenValidation(accessToken)
    }

    private suspend fun executeTokenValidation(accessToken: String): TokenValidationResponse = coroutineScope {
        withContext(Dispatchers.IO) {
            webClientBuilder.build()
                    .post()
                    .uri(validateAccessTokenUrl)
                    .bodyValue(TokenValidationRequest(accessToken = accessToken))
                    .awaitExchange()
                    .awaitBody<TokenValidationResponse>()
        }
    }
}




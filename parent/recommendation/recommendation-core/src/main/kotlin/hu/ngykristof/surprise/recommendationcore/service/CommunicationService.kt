package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.recommendationcore.util.asyncTask
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import org.springframework.web.util.UriBuilder

@Service
class CommunicationService(
        private val webClientBuilder: WebClient.Builder
) {

    fun executeRequest(url: String) = asyncTask {
        executeETLFlow(url)
    }

    private suspend fun executeETLFlow(url: String) {
        webClientBuilder.build()
                .post()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path(url)
                            .build()
                }
                .awaitExchange()
                .awaitBody<Unit>()
    }
}
package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.commonscore.util.logger
import hu.ngykristof.surprise.recommendationcore.service.ETLConstants.Companion.START_UP
import hu.ngykristof.surprise.recommendationcore.service.ETLConstants.Companion.UPDATE_MOVIES
import hu.ngykristof.surprise.recommendationcore.util.asyncTask
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import org.springframework.web.util.UriBuilder
import java.time.OffsetDateTime


@Service
class ETLService(
        private val webClientBuilder: WebClient.Builder,
        private val environment: Environment,
        @Value("\${surprise.etl.django-base-url}") private val etlBaseUrl: String
) {

    val logger = logger()

    fun runSetupETLFlow() = asyncTask {
        executeETLFlow(url = "$etlBaseUrl/$START_UP")

        logger.debug("Setup ETL flow was started at : ${OffsetDateTime.now()}")
    }

    fun runUpdateMoviesETLFlow() = asyncTask {
        executeETLFlow(url = "$etlBaseUrl/$UPDATE_MOVIES")

        logger.debug("Update movie ETL flow was started at : ${OffsetDateTime.now()}")
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

package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.commonscore.util.logger
import hu.ngykristof.surprise.recommendationcore.util.asyncTask
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
        private val environment: Environment
) {

    val logger = logger()

    fun runSetupETLFlow() = asyncTask {
        executeETLFlow(ETLConstants.START_UP_URL)

        logger.debug("Setup ETL flow was started at : ${OffsetDateTime.now()}")
    }

    fun runUpdateMoviesETLFlow() = asyncTask {
        executeETLFlow(ETLConstants.UPDATE_MOVIES_URL)

        logger.debug("Update movie ETL flow was started at : ${OffsetDateTime.now()}")
    }


    private suspend fun executeETLFlow(url: String) {
        webClientBuilder.build()
                .post()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path(url)
                            .queryParam(ETLConstants.PROFILE_PARAM_KEY, getActiveProfile())
                            .build()
                }
                .awaitExchange()
                .awaitBody<Unit>()
    }

    private fun getActiveProfile(): String = if (environment.activeProfiles.contains("prod")) {
        ETLConstants.PROD_PROFILE
    } else {
        ETLConstants.DEBUG_PROFILE
    }

}

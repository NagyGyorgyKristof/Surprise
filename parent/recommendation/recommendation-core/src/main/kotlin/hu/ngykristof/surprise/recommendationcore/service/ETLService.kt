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

    //TODO mostantol django EtlService-t kell hivni REST-en o fogja majd futtatni a python scripteket

    fun runSetUpETLFlow() = asyncTask {
        //TODO elkerni a current profile-t es ugy az alapjan elkuldeni!
        executeETLFlow(ETLConstants.START_UP_URL)
        logger.debug("Setup ETL flow was started at : ${OffsetDateTime.now()}")
    }

    fun runUpdateMoviesETLFlow() = asyncTask {
        //TODO elkerni a current profile-t es ugy az alapjan elkuldeni!
        executeETLFlow(ETLConstants.UPDATE_MOVIES_URL)
        logger.debug("Update movie ETL flow was started at : ${OffsetDateTime.now()}")
    }


    private suspend fun executeETLFlow(etlFlowUrl: String) {
        webClientBuilder.build()
                .post()
                .uri { uriBuilder: UriBuilder ->
                    uriBuilder.path(etlFlowUrl)
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

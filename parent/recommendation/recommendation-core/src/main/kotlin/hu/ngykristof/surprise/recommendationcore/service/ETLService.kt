package hu.ngykristof.surprise.recommendationcore.service

import hu.ngykristof.surprise.commonscore.util.logger
import hu.ngykristof.surprise.recommendationcore.service.ETLConstants.Companion.START_UP
import hu.ngykristof.surprise.recommendationcore.service.ETLConstants.Companion.UPDATE_MOVIES
import hu.ngykristof.surprise.recommendationcore.util.asyncTask
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.OffsetDateTime


@Service
class ETLService(
        val communicationService: CommunicationService,
        @Value("\${surprise.etl.django-base-url}") private val etlBaseUrl: String
) {

    val logger = logger()

    fun runSetupETLFlow() = asyncTask {
        communicationService.executeRequest(url = "$etlBaseUrl/$START_UP")

        logger.debug("Setup ETL flow was started at : ${OffsetDateTime.now()}")
    }

    fun runUpdateMoviesETLFlow() = asyncTask {
        communicationService.executeRequest(url = "$etlBaseUrl/$UPDATE_MOVIES")

        logger.debug("Update movie ETL flow was started at : ${OffsetDateTime.now()}")
    }
}
